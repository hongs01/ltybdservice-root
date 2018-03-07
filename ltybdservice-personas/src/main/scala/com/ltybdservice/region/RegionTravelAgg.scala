package com.ltybdservice.region

import com.ltybdservice.config.FilterParam
import com.ltybdservice.region.RegionTravelBean.{RegionAnalysis, _}
import com.ltybdservice._
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.collection.mutable

/**
  * 聚合求用户行程
  */
object RegionTravelAgg {
  lazy val minUserInfoSize= FilterParam.param.getMinUserInfoSize
  lazy val odInfoSize= FilterParam.param.getOdInfoSize
  /**
    *
    * @param spark
    * @param userRealTimeRegion
    *计算用户换乘行程
    */
  def getTransferRegionTravel(spark: SparkSession, userRealTimeRegion: Dataset[UserRealTimeRegion]): Dataset[UserTimeRegionTransferTravel] = {
    import spark.implicits._
    /**
      * 假设数据按星期呈现规律，基于此假设进行计算，每个工作日上午去上班，下午回家，周五下班可能去其他地方，周末上午去一个地方，下午回来，可分为5类时间
     *因此可以以用户手机号，城市，时间类型进行分组
      */
    val userTimeGroup = userRealTimeRegion.groupByKey(userRealTimeRegion => {
      val user = userRealTimeRegion.userRealTime.user
      UserWeekGroupKey(user.phoneModel, user.cityCode, userRealTimeRegion.timeType)
    })
    /**
      * 对分组后的数据进行任意聚合，得到分组时间内的行程信息，在分组时间内只有一个行程
      */
    val regionTravels = userTimeGroup.mapGroups(RegionTravelAgg.regionAggregator(_,_)).filter(_.validate())
    /**
      * 将内部计算结果转换为能输出到mysql数据库的形式
      */
    val transferTravels = regionTravels.map(regionTravel => {
      val user=regionTravel.user
      val t=regionTravel.travel
      val startHourMinutesSeconds = Transform.daySeconds2HourMinutesSeconds(t.startDaySeconds)
      val endHourMinutesSeconds = Transform.daySeconds2HourMinutesSeconds(t.endDaySeconds)
//      val originLocation = regionTravel.originRegion.location
//      val destLocation = regionTravel.destRegion.location
//      val transfer = Transform.transfer(originLocation.longitude, originLocation.latitude, destLocation.longitude, destLocation.latitude, regionTravel.cityCode)
      UserTimeRegionTransferTravel(user.userId,user.phoneModel, user.cityCode,regionTravel.timeType, t.originRegion.toString(),t.originPointCount,t.originDateCount, startHourMinutesSeconds, t.destRegion.toString(),t.destPointCount,t.destDateCount, endHourMinutesSeconds, true)
    }).filter(_.validate())
    transferTravels
  }

  /**
    * spark  mapGroups分组聚合函数
    * @param key  分组的键值
    * @param userRealTimeRegionIt 按时间类型分组的数据
    * @return 聚合分组时间内的用户行程，分组时间内只有一个行程，过滤不对的行程
    */
  def regionAggregator(key: UserWeekGroupKey, userRealTimeRegionIt: Iterator[UserRealTimeRegion]): UserTimeTravel = {
    //构造空行程，用于返回无效行程
    val zeroUser=User("","", "")
    val zeroRegion=Region(Location(0,0),"")
    val zeroTravel=Travel(zeroRegion,0,0,0, zeroRegion,0,0,0,0,0)
    val zeroUserTimeTravel = UserTimeTravel(zeroUser,"",zeroTravel)
    //构建区域到用户在区域的信息量的映射
    val zeroMutableHashMap = mutable.HashMap[Region, RegionAnalysis]()
    // 如果数据太少则返回会被过滤的值,注意Iterator要转为list使用  Iterator是懒序列，只有在用的时候才会计算，通过toList，将它全部计算出来
    val userRealTimeRegionList = userRealTimeRegionIt.toList
    //如果用户在类型时间内上传的数据量没有达到指定的值，则用户数据无效，直接返回空行程
    if (userRealTimeRegionList.size < minUserInfoSize) {
      zeroUserTimeTravel
    } else {
      // 聚合统计用户在每个区域的信息量
      val rawRegionLongMap = userRealTimeRegionList.foldLeft(zeroMutableHashMap)(RegionTravelAgg.foldLeftRegionAnalysis(_,_))
      //如果始终聚合在一个上，说明用户没有行程，返回空行程
      if (rawRegionLongMap.size < 2) {
        zeroUserTimeTravel
      } else{
        // 按区域次数降序用户区域信息
        val sortRegionLongList = rawRegionLongMap.toList.sortWith(_._2.pointCount > _._2.pointCount)
        //如果用户在起始地或目的地上传的数据量没有达到指定的值，则用户数据无效，直接返回空行程
        if(sortRegionLongList(0)._2.pointCount<odInfoSize||sortRegionLongList(1)._2.pointCount<odInfoSize){
          zeroUserTimeTravel
        }else{
          //取区域次数最多的两个区域，如果用户行程是有规律的，次数最多的两个区域最有可能是od点
          //odRegion是一个4元组依次为最大信息量区域及区域数据统计，次大信息量区域及区域数据统计
          val odRegion = (sortRegionLongList(0)._1,sortRegionLongList(0)._2, sortRegionLongList(1)._1,sortRegionLongList(1)._2)
          //过滤非od点用户信息，只保留用户在od点的信息，注意在此之后只有od点的用户信息了
          val filterUserRealTimeRegionAnalysisList = userRealTimeRegionList.filter(u => (u.region == odRegion._1 || u.region == odRegion._3))
          //将区域统计信息添加到用户历史信息中
          val odUserRealTimeRegionAnalysisList=filterUserRealTimeRegionAnalysisList.map(userRealTimeRegion=>{
              if(userRealTimeRegion.region==odRegion._1){
                val analysis = odRegion._2
                UserRealTimeRegionAnalysis(userRealTimeRegion,analysis.pointCount,analysis.dateList.size)
              }else{
                val analysis = odRegion._4
                UserRealTimeRegionAnalysis(userRealTimeRegion,analysis.pointCount,analysis.dateList.size)
              }
            })
          //按日期（天）对用户数据分组
          val dayUser = odUserRealTimeRegionAnalysisList.groupBy(userRegionAnalysis => new java.sql.Date(userRegionAnalysis.userRealTimeRegion.userRealTime.currentTime.getTime).toString)
          //聚合按天分组后的行程信息
          dayUser.foldLeft(zeroUserTimeTravel)(RegionTravelAgg.foldLeftRegionTravel(_,_))
        }
      }
    }
  }

  /**
    * scala foldLeft函数不断将新的分组数据聚合进已统计的分组数据，第一个参数是已聚合分组统计数据，第二个参数是待统计分组数据
    * @param regionAnalysis 已聚合分组统计数据
    * @param userRealTimeRegion
    * @return 返回用户在每个区域的统计信息
    */
  def foldLeftRegionAnalysis(regionAnalysis: mutable.HashMap[Region, RegionAnalysis], userRealTimeRegion: UserRealTimeRegion): mutable.HashMap[Region, RegionAnalysis] = {
    val analysis = regionAnalysis.get(userRealTimeRegion.region)
    import java.util.Calendar
    val cal = Calendar.getInstance
    cal.setTime(userRealTimeRegion.userRealTime.currentTime)
    val weekDay = cal.get(Calendar.DAY_OF_WEEK)
    if (analysis != None) {
      val dateList=analysis.get.dateList
      val newDateList =if(dateList.contains(weekDay)){
        dateList
      }else{
        weekDay::dateList
      }
      regionAnalysis.put(userRealTimeRegion.region, RegionAnalysis((analysis.get.pointCount + 1),newDateList))
      regionAnalysis
    } else {
      regionAnalysis.put(userRealTimeRegion.region, RegionAnalysis(1,List[Int](weekDay)))
      regionAnalysis
    }
  }

  /**
    * scala foldLeft函数不断将新的分组数据聚合进已统计的分组数据，第一个参数是已聚合分组统计数据，第二个参数是待统计分组数据
    * @param userTimeTravel   已聚合分组统计数据
    * @param userRealTimeRegionAnalysisList 用户信息按天分组的数据，包含在出发地，目的地两个区域的信息
    * @return 聚合按天分组后的行程信息
    */
  def foldLeftRegionTravel(userTimeTravel: UserTimeTravel, userRealTimeRegionAnalysisList: (String, List[UserRealTimeRegionAnalysis])): UserTimeTravel = {
    val zeroUser=User("","", "")
    val zeroRegion=Region(Location(0,0),"")
    val zeroTravel=Travel(zeroRegion, 0,0,0, zeroRegion, 0,0,0, 0, 0)
    //构造空的用户行程，用于返回无效行程
    val zeroUserTimeTravel = UserTimeTravel(zeroUser,"",zeroTravel )
    //可变列表，用于存储某天的不定个数的行程，分组后的输入数据只有两个区域的数据，所以所有行程只会在两个固定的区域之间来回
    val travelBuffer = new mutable.ListBuffer[Travel]()
    //对某天的用户信息按时间从早到晚进行排序
    val sortUserRealTimeRegionAnalysis = userRealTimeRegionAnalysisList._2.sortWith(
      (u1,u2)=> {
      val t1=u1.userRealTimeRegion.userRealTime.currentTime
      val t2=u2.userRealTimeRegion.userRealTime.currentTime
      t1.getTime < t2.getTime
    })
    //获取按时间排序后用户数据的迭代器
    val it = sortUserRealTimeRegionAnalysis.iterator
    val userRealTimeRegionAnalysis = it.next()
    val userRealTimeRegion = userRealTimeRegionAnalysis.userRealTimeRegion
    //设置起始地点信息为排序后第一个数据的信息
    var originRegion: Region = userRealTimeRegion.region
    var originPointCount:Int = userRealTimeRegionAnalysis.pointCount
    var originDateCount :Int = userRealTimeRegionAnalysis.dateCount
    var startDaySeconds: Long = Transform.timestamp2DaySeconds(userRealTimeRegion.userRealTime.currentTime)
    while (it.hasNext) {

      val itUserRealTimeRegionAnalysis = it.next()
      val itUserRealTimeRegion=itUserRealTimeRegionAnalysis.userRealTimeRegion
      if (originRegion == itUserRealTimeRegion.region) {

        //不断更新出发时间
        startDaySeconds = Transform.timestamp2DaySeconds(itUserRealTimeRegion.userRealTime.currentTime)
      } else {
        /**
          * originRegion,startDaySeconds都是可变的,将行程添加到当天行程列表
          * 如果新的区域名与上一个记录的区域名不一样，说明到了目的地，确定一趟行程
          */
        val endDaySeconds = Transform.timestamp2DaySeconds(itUserRealTimeRegion.userRealTime.currentTime)
        val userRealTime = itUserRealTimeRegion.userRealTime
        val destPointCount=itUserRealTimeRegionAnalysis.pointCount
        val destDateCount=itUserRealTimeRegionAnalysis.dateCount
        //得到某天内的一个行程
        val travel=Travel(originRegion,originPointCount,originDateCount, startDaySeconds, itUserRealTimeRegion.region,destPointCount,destDateCount,endDaySeconds, endDaySeconds - startDaySeconds, 0)
        //将本次计算得到的行程保存
        travelBuffer.append(travel)
        //保存完已知行程后，将新的用户记录作为起始地点，计算当天是否有新的行程
        originRegion = itUserRealTimeRegion.region
        originPointCount=itUserRealTimeRegionAnalysis.pointCount
        originDateCount=itUserRealTimeRegionAnalysis.dateCount
        startDaySeconds = Transform.timestamp2DaySeconds(userRealTime.currentTime)
      }
    }
    //分析某天的所有行程
    //没有行程，在某个类型时间内必定有且只有两个区域，但是在某天内，可能只有一个区域有数据，导致没有行程
    if(travelBuffer.isEmpty){
      zeroUserTimeTravel
    }else{
      //取当天行程最长的记录作为行程
      val maxTravel = travelBuffer.maxBy(_.interval)
      //求平均时间，如果是第该时间类型内的一条数据则直接返回所得行程
      if (!userTimeTravel.validate()) {
        UserTimeTravel(userRealTimeRegion.userRealTime.user,userRealTimeRegion.timeType,maxTravel)
      } else if (userTimeTravel.travel.originRegion != maxTravel.originRegion) {
      //如果相邻两天方向不一致，返回zero值
      zeroUserTimeTravel
    } else {
        //将当天行程信息聚合进之前天数的统计信息，求取时间平均信息
        val travel=userTimeTravel.travel
        val avgStartDaySeconds = (travel.startDaySeconds * travel.validTravelCount + maxTravel.startDaySeconds) / (travel.validTravelCount + 1)
        val avgEndDaySeconds = (travel.endDaySeconds * travel.validTravelCount + maxTravel.endDaySeconds) / (travel.validTravelCount + 1)
        val newTravel=Travel(travel.originRegion,travel.originPointCount,travel.originDateCount, avgStartDaySeconds, travel.destRegion,travel.destPointCount,travel.destDateCount, avgEndDaySeconds, avgEndDaySeconds - avgStartDaySeconds, travel.validTravelCount)
        UserTimeTravel(userTimeTravel.user,userTimeTravel.timeType,newTravel)
      }
    }
  }
}
