package com.lantaiyuan

import java.util

import com.alibaba.fastjson.JSON
import com.lantaiyuan.bean.{GW_APP, INOUTPARK, INSTATION, OUTSTATION}
import com.lantaiyuan.common.constEnum.KafkaConsum
import com.lantaiyuan.start.imp.RedisClient
import com.lantaiyuan.utils.{DateUtil, RedisConf}
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import redis.clients.jedis.{Jedis, JedisCluster}

import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object ScalaMain {
  case class Coordinate(@BeanProperty lon: Double,@BeanProperty lat: Double,@BeanProperty eventTime: Long){
  }
  //老网关
  private var stream_HANDAN: DStream[(String, String)] = null;
  private var stream_HANDAN_topic: DStream[(String, String)] = null

  //新网关
  private var stream_Gis: DStream[(String, String)] = null;
  private var stream_Gis_HD: DStream[(String, String)] = null;
  private var stream_StationIO: DStream[(String,String)] =null;
  private var stream_ITS_Topic_GIS: DStream[(String, String)] = null
  private var stream_ITS_Topic_GIS_HD: DStream[(String, String)] = null
  private var stream_ITS_Topic_StationIO:DStream[(String, String)] = null

  private var unifiedStream: DStream[(String, String)] = null


  var kfkMapOld: Map[String, String] = Map("bootstrap.servers"->"10.1.254.11:6667,10.1.254.12:6667,10.1.254.13:6667","key.deserializer"->"kafka.serializer.StringEncoder","value.deserializer"->"kafka.serializer.StringEncoder","group.id"->"sparkMain","auto.offset.reset"->"largest")
  var kfkMapNew: Map[String, String] = Map("bootstrap.servers"->"10.1.254.88:9092","key.deserializer"->"kafka.serializer.StringEncoder","value.deserializer"->"kafka.serializer.StringEncoder","group.id"->"sparkMain","auto.offset.reset"->"largest")

  val data = new util.HashMap[String, util.Map[String, util.List[Coordinate]]]
  val data_HD = new util.HashMap[String, util.Map[String, util.List[Coordinate]]]

  val mysqldata = new MysqlData()
  val conf = new SparkConf().setAppName("scalaMainS").setMaster("local[*]")
  val stramingContext = new StreamingContext(conf, Seconds(1))



  /**
    * 创建一个从kafka获取数据的流.
    * @param scc           spark streaming上下文
    * @param kafkaParam    kafka相关配置
    * @param topics        需要消费的topic集合
    * @return
    */
  def createStream(scc: StreamingContext, kafkaParam: Map[String, String], topics: Set[String]) = {
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](scc, kafkaParam, topics)
  }


  def main(args: Array[String]): Unit = {

    //接收老网关topic下的流
    stream_HANDAN = kafkaConsumerInputStream(kfkMapOld, Set[String](KafkaConsum.GW2APP.getKey), 1)
    stream_HANDAN_topic = stream_HANDAN.map(x => ("130400@old", x._2))

    //接收ITS_Topic_GIS下的流
    stream_Gis = kafkaConsumerInputStream(kfkMapNew, Set[String](KafkaConsum.ITS_Topic_GIS.getKey), 1)
    stream_ITS_Topic_GIS = stream_Gis.map(x => (KafkaConsum.ITS_Topic_GIS.getKey + "@" + "new", x._2))
    stream_Gis_HD = kafkaConsumerInputStream(kfkMapNew, Set[String](KafkaConsum.ITS_Topic_GIS_HD.getKey), 1)
    stream_ITS_Topic_GIS_HD = stream_Gis_HD.map(x => (KafkaConsum.ITS_Topic_GIS_HD.getKey + "@" + "new", x._2))

    //接收进出站数据ITS_Topic_StationIO下的流
    stream_StationIO =kafkaConsumerInputStream(kfkMapNew, Set[String](KafkaConsum.ITS_Topic_StationIO.getKey),1)
    stream_ITS_Topic_StationIO = stream_Gis.map(x => (KafkaConsum.ITS_Topic_StationIO.getKey + "@" + "new", x._2))

    var kafkaStreams: ListBuffer[DStream[(String, String)]] = ListBuffer[DStream[(String, String)]]()
    kafkaStreams.append(stream_HANDAN_topic)
    kafkaStreams.append(stream_ITS_Topic_GIS)
    kafkaStreams.append(stream_ITS_Topic_GIS_HD)
    kafkaStreams.append(stream_ITS_Topic_StationIO)
    //kafkaStreams.append(stream_ITS_Topic_ParkIO)
    unifiedStream =stramingContext.union(kafkaStreams)
    /*unifiedStream.print()*/
    unifiedStream.filter(_._2 != "")
      .map(x => {
        try {
          val topic_namecode = x._1.split("@")(0)
          val topic_type = x._1.split("@")(1)
          val jon = JSON.parseObject(x._2)

          //读取老网关gps数据
          if (topic_type == "old" && jon.containsKey("packetType") && jon.get("packetType").toString == "gps") {
            val gw_app = JSON.parseObject(jon.toString, classOf[GW_APP])
            val cityCode = topic_namecode
            val lineId = gw_app.getGprsId
            val direction = gw_app.getDirection
            val cid = gw_app.getVehicleId
            val start_time = mysqldata.getStartTimeByLidDir(cityCode, lineId, direction, gw_app)
            val end_time = mysqldata.getEndTimeByLidDir(cityCode, lineId, direction, gw_app)
            (cityCode + "_" + cid + "_" + direction, start_time, end_time, x._1, jon)
            //根据进站包查询
          } else if (topic_type == "old" && jon.containsKey("packetType") && jon.get("packetType").toString == "inStation") {
            val inStation = JSON.parseObject(jon.toString, classOf[INSTATION])
            val cityCode = topic_namecode
            val lineId = inStation.getGprsId
            val direction = inStation.getDirection
            val cid = inStation.getVehicleId
            var start_time = "";
            val end_time = mysqldata.getEndTimeByLidDir1(cityCode, lineId, direction, inStation)
            if (inStation.getCurrStationNo == 1) start_time = inStation.getEventTime
            (cityCode + "_" + cid + "_" + direction, start_time, end_time, x._1, jon)
            //根据出站包查询
          } else if (topic_type == "old" && jon.containsKey("packetType") && jon.get("packetType").toString == "outStation") {
            val outStation = JSON.parseObject(jon.toString, classOf[OUTSTATION])
            val cityCode = topic_namecode
            val lineId = outStation.getGprsId
            val direction = outStation.getDirection
            val cid = outStation.getVehicleId
            var start_time = ""
            val end_time = mysqldata.getEndTimeByLidDir2(cityCode, lineId, direction, outStation)
            if (outStation.getCurrStationNo == 1) start_time = outStation.getOutStationTime
            (cityCode + "_" + cid + "_" + direction, start_time, end_time, x._1, jon)
            //根据进出场包查询
          } else if (topic_type == "old" && jon.containsKey("packetType") && jon.get("packetType").toString == "inoutPark") {
            val inoutPark = JSON.parseObject(jon.toString, classOf[INOUTPARK])
            val cityCode = topic_namecode
            val cid = inoutPark.getVehicleId
            val direction = inoutPark.getDirection
            var start_time = ""
            var end_time = ""
            if (inoutPark.getFlag == 0) start_time = inoutPark.getEventTime
            if (inoutPark.getFlag == 1) end_time = inoutPark.getEventTime
            (cityCode + "_" + cid + "_" + direction, start_time, end_time, x._1, jon)
          } else if (topic_type == "new" && topic_namecode=="ITS_Topic_GIS") {
            val jsonGis = JSON.parseObject(jon.get("body").toString)
            val cid = jsonGis.get("dev_id")
            val direction = jsonGis.get("direction").toString.toInt
            val lineId = jsonGis.get("line_id").toString.toInt
            val cityCode = jsonGis.get("city_code").toString
            val start_time = mysqldata.getStartTimeByLidDirG(cityCode, lineId, direction, jsonGis)
            val end_time = mysqldata.getEndTimeByLidDirG(cityCode, lineId, direction, jsonGis)
            (cityCode + "_" + cid + "_" + direction, start_time, end_time, x._1, jsonGis)
          } else if (topic_type =="new" && topic_namecode=="ITS_Topic_StationIO"){
            val jsonSio=JSON.parseObject(jon.get("body").toString)
            val cid = jsonSio.get("dev_id")
            val direction = jsonSio.get("direction").toString.toInt
            val lineId = jsonSio.get("line_id").toString.toInt
            val cityCode = jsonSio.get("city_code").toString
            val next_station_no=jsonSio.get("next_station_no").toString
            var start_time =""
            if(next_station_no=="2") start_time=jsonSio.get("gps_time").toString
            val end_time = mysqldata.getEndTimeByLidDirNew(cityCode, lineId, direction, jsonSio)
            (cityCode + "_" + cid + "_" + direction, start_time, end_time, x._1, jsonSio)
          }
          else {
            ("", "", "", "", "")
          }
        } catch {
          case e: Exception => e.printStackTrace()
            null
        }
      }).filter(_._1!="")
      .filter(_._4!="")
      .repartition(1)
      .foreachRDD(rdd => {

        rdd.foreach(pair => {
        //  val jedis = RedisConf.jc
           val jedis = RedisClient.pool.getResource
          //存储最新20个gps点，来源于多个kafka  (3个)  (redis集合)
          var gpsDataList: util.List[Coordinate] = null
          if (pair._4.split("@")(1) == "old") {
            //   println("old-----"+JSON.parseObject(pair._5.toString).get("eventTime"))
            val key = getKey(pair._4, pair._5.toString)
            var routeBusMap = data.get(key)
            if (routeBusMap == null) routeBusMap = new util.HashMap[String, util.List[Coordinate]]()
            val runStatus = JSON.parseObject(pair._5.toString).get("runstatus")

            if (runStatus != 0 && JSON.parseObject(pair._5.toString).get("packetType").toString == "gps") {
              val vehicleId = JSON.parseObject(pair._5.toString).get("vehicleId").toString
              if (routeBusMap.containsKey(vehicleId)) {
                val gpsData = routeBusMap.get(vehicleId)
                if (!gpsData.isEmpty && gpsData.size() > 0) {
                  gpsDataList = gpsData
                }
              } else {
                gpsDataList = new util.LinkedList[Coordinate]
              }
              val lon = JSON.parseObject(pair._5.toString).get("longitude").toString.toDouble
              val lat = JSON.parseObject(pair._5.toString).get("latitude").toString.toDouble
              val eventTime = JSON.parseObject(pair._5.toString).get("eventTime")

              val cdi = new Coordinate(lon, lat, DateUtil.formatTimeStr2TimeStamp(eventTime.toString))

              gpsDataList.add(0, cdi)

              routeBusMap.put(vehicleId, mysqldata.getSubList(gpsDataList))
              jedis.set(key, JSON.toJSON(routeBusMap).toString)
               println(key+"---"+jedis.get(key))
              data.put(key, routeBusMap)
            }
          } else if (pair._4.startsWith("ITS_Topic_GIS")) {

            val json = JSON.parseObject(pair._5.toString)
            val time=json.get("gps_time").toString.toLong
            val carId = json.get("dev_id").toString

            val runStatus = json.get("vehicle_status")
            //0x00表示正常，其他表示异常
            if (runStatus == 1) {
              val key = getITSKey(json.toString)
              var routeBusMap = data.get(key)
              if (routeBusMap == null) routeBusMap = new util.HashMap[String, util.List[Coordinate]]()
              if (routeBusMap.containsKey(json.get("dev_id").toString)) {
                val gpsDatas = routeBusMap.get(json.get("dev_id").toString)
                if (!gpsDatas.isEmpty && gpsDatas.size() > 0) {
                  gpsDataList = gpsDatas
                }
              } else {
                gpsDataList = new util.LinkedList[Coordinate]
              }
              val lon = json.get("lon").toString.toDouble
              val lat = json.get("lat").toString.toDouble
              val eventTime = json.get("gps_time").toString.toLong*1000
              val coordinate = new Coordinate(lon, lat, eventTime)
              gpsDataList.add(0, coordinate)

              routeBusMap.put(json.get("dev_id").toString, mysqldata.getSubList(gpsDataList))
              jedis.set(key, JSON.toJSON(routeBusMap).toString)
println(jedis.get(key))
              data.put(key, routeBusMap)
            }
            // 高频次的gps点，来源于ITS_Topic_GIS_HD(5s/次)  (1个) 存储20个点(经纬度+时间(时间戳)  json串)
            if (runStatus=="1" && pair._4.split("@")(0) =="ITS_Topic_GIS_HD"){
              val key=getITSKeyHD(json.toString)
              var routeBusMap=data_HD.get(key)
              if (routeBusMap == null) routeBusMap = new util.HashMap[String, util.List[Coordinate]]
              if (routeBusMap.containsKey(json.get("dev_id").toString)){
                val gpsDatas= routeBusMap.get(json.get("dev_id").toString)
                if (!gpsDatas.isEmpty && gpsDatas.size()>0){
                  gpsDataList =gpsDatas
                }
              }else {
                gpsDataList = new util.LinkedList[Coordinate]
              }
              val lon=json.get("lon").toString.toDouble
              val lat=json.get("lat").toString.toDouble
              val eventTime=json.get("gps_time").toString.toLong*1000
              val coordinate = new Coordinate(lon, lat, eventTime)
              gpsDataList.add(0, coordinate)

              routeBusMap.put(json.get("dev_id").toString, mysqldata.getSubList(gpsDataList))
              jedis.set(key,JSON.toJSON(routeBusMap).toString)
              // println(key+jedis.get(key))
              data_HD.put(key,routeBusMap)
            }
          }

          // 首站时间，末站时间，来源于多个kafka  (3个)
          if (pair._2 != "" || pair._3 != "") {
            var jobj = new com.alibaba.fastjson.JSONObject()
            if (pair._4.split("@")(1) == "old") {
              jobj += ("start_time" -> (if (pair._2 == "") readStime("settle_travltime_" + pair._1, jedis) else DateUtil.formatTimeStr2TimeStamp(pair._2)))
              jobj += ("end_time" -> (if (pair._3 == "") "" else DateUtil.formatTimeStr2TimeStamp(pair._3)))
              jedis.set("settle_travltime_" + pair._1, jobj.toString)
              jedis.expire("settle_travltime_" + pair._1, 24 * 60 * 3600)
            } else {
              val json=JSON.parseObject(pair._5.toString)
              if(json.get("gps_time")!="0"){
                jobj += ("start_time" -> (if (pair._2 == "") readStime("settle_travltime_" + pair._1, jedis) else (pair._2.toLong*1000).toString))
                jobj += ("end_time" -> (if (pair._3 == "") "" else (pair._3.toLong*1000).toString))
                jedis.set("settle_travltime_" + pair._1, jobj.toString)
                jedis.expire("settle_travltime_" + pair._1, 24 * 60 * 3600)
              }
            }
          }
           RedisClient.pool.returnResource(jedis)
        })
      })
    stramingContext.start()
    stramingContext.awaitTermination()
  }

  def kafkaConsumerInputStream(kafkaConfig:Map[String,String],kafkaConsums:Set[String],windowLength:Int):DStream[(String,String)]={
   // val stramingContext = SparkConnection.streamingContext()
    var stream:InputDStream[(String,String)]= null;
      stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](stramingContext,kafkaConfig,kafkaConsums)
    if(windowLength>0 && windowLength%1 == 0){
      stream.window(Seconds(windowLength),Seconds(windowLength))
    }else{
      stream.window(Seconds(1),Seconds(1))
    }
  }

  def getKey(cct: String, str: String): String = {
    val json = JSON.parseObject(str)
    val lineid = json.get("gprsId").toString
    val direction = json.get("direction").toString
    val carId = json.get("vehicleId").toString
    "settle_travl" + "_" + cct.split("@")(0).concat("_").concat(lineid).concat("_").concat(direction).concat("_").concat(carId)
  }

  def getITSKey(str: String): String = {
    val json = JSON.parseObject(str)
    val citycode = json.get("city_code").toString
    val lineid = json.get("line_id").toString
    val direction = json.get("direction").toString
    val carId = json.get("dev_id").toString
    "settle_travl" + "_" + citycode.concat("_").concat(lineid).concat("_").concat(direction).concat("_").concat(carId)
  }

  def getITSKeyHD(str: String): String = {
    val json = JSON.parseObject(str)
    val citycode = "130400"
    //json.get("city_code").toString
    val lineid = json.get("line_id").toString
    val direction = "0"
    //json.get("direction").toString
    val carId = json.get("dev_id").toString
    "settle_travl_HD_" + "_" + citycode.concat("_").concat(lineid).concat("_").concat(direction).concat("_").concat(carId)
  }

  def readStime(str: String, jedis: Jedis): String = {
    val result = jedis.get(str)
    if (result != null) JSON.parseObject(result).get("start_time").toString
    else ""
  }
  //读取开始时间
  def readStimeCluster(str: String, jedis: JedisCluster): String = {
    val result = jedis.get(str)
    if(result!=null){
      val json=JSON.parseObject(result)
      if (json.get("start_time").toString!=""){
        if (json.get("start_time").toString.contains("-")){
          val stime= DateUtil.formatTimeStr2TimeStamp(json.get("start_time").toString)
          val etime=readEtimeCluster(str,jedis)
          if(stime<etime){
            stime.toString
          }else{
            var jobj = new com.alibaba.fastjson.JSONObject()
            jobj += ("start_time" ->stime.toString )
            jobj += ("end_time" ->"")
            jedis.set(str,jobj.toString)
            stime.toString
          }
        }else{
          val stime=  json.get("start_time").toString.toLong
          val etime=readEtimeCluster(str,jedis)
          if(stime<etime){
            stime.toString
          }else{
            var jobj = new com.alibaba.fastjson.JSONObject()
            jobj += ("start_time" ->stime.toString )
            jobj += ("end_time" ->"")
            jedis.set(str,jobj.toString)
            stime.toString
          }
        }
      }else{
        ""
      }
    }
    else {
      ""
    }
  }



  //读取结束时间
  def readEtimeCluster(str: String, jedis: JedisCluster): Long = {
    val result = jedis.get(str)
    val json=JSON.parseObject(result)
    if (json.get("end_time").toString!=""){
      if (json.get("end_time").toString.contains("-")){
        DateUtil.formatTimeStr2TimeStamp(json.get("end_time").toString)
      }else{
        json.get("end_time").toString.toLong
      }
    }else{
      0L
    }

  }
}
