package com.ltybdservice.region

import com.ltybdservice.config.FilterParam
import com.ltybdservice.{Region, User, UserRealTimeRegion, Validate}


object RegionTravelBean {
  lazy val minInterval=FilterParam.param.getMinInterval
  lazy val maxInterval=FilterParam.param.getMaxInterval

  /**
    * 按用户、周规律分组的key
    * @param phoneModel
    * @param cityCode
    * @param timeType
    */
  case class UserWeekGroupKey(phoneModel:String, cityCode:String, timeType:String)

  /**
    *用户在特定区域的信息量（数据量，日期列表）
    * @param pointCount
    * @param dateList
    */
  case class RegionAnalysis(pointCount:Int,dateList:List[Int])
  /**
    * 包含用户在区域内发送的数据量，日期数
    * @param userRealTimeRegion
    * @param pointCount
    * @param dateCount
    */
  case class UserRealTimeRegionAnalysis(userRealTimeRegion:UserRealTimeRegion,pointCount:Int,dateCount:Int)
  /**
    *用户行程，包含起始地，目的地，出发时间，到达时间，用时，有效的统计记录次数
    * @param originRegion
    * @param startDaySeconds
    * @param destRegion
    * @param endDaySeconds
    * @param interval
    * @param validTravelCount
    */
  case class Travel(originRegion: Region,originPointCount:Int,originDateCount:Int, startDaySeconds: Long, destRegion: Region,destPointCount:Int,destDateCount:Int, endDaySeconds: Long, interval: Long, validTravelCount: Long)

  /**
    *用户在某类型时间的行程
    * @param user
    * @param timeType
    * @param travel
    */
  case class UserTimeTravel(user:User, timeType:String,travel:Travel) extends Validate {
    override def validate() = {
      if((!user.validate())||timeType.isEmpty||travel.interval<minInterval||travel.interval>maxInterval){
        false
      }else{
        true
      }
    }
  }

  /**
    *最终对外输出形式
    * @param userId
    * @param cityCode
    * @param originRegion
    * @param startDayTime
    * @param destRegion
    * @param endDayTime
    * @param transfer
    */
  case class UserTimeRegionTransferTravel(userId: String, phoneModel: String, cityCode: String, timeType:String, originRegion: String,originPointCount:Int,originDateCount:Int, startDayTime: String, destRegion: String,destPointCount:Int,destDateCount:Int, endDayTime: String, transfer: Boolean) extends Validate {
    override def validate() = {
      if((userId.isEmpty&&phoneModel.isEmpty)||cityCode.isEmpty||timeType.isEmpty){
        false
      }else{
        true
      }
    }
  }
}