/*
package com.lantaiyuan.start.imp

import java.util
import java.util.{HashMap, LinkedList, List}

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import com.lantaiyuan.bean._
import com.lantaiyuan.common.connection.SparkConnection
import com.lantaiyuan.common.constEnum.KafkaConsum
import com.lantaiyuan.start.SparkStart
import com.lantaiyuan.staticCache.imp.EBUS
import com.lantaiyuan.utils.{DateUtil, RedisConf}
import org.apache.spark.streaming.dstream.DStream
import redis.clients.jedis.{Jedis, JedisCluster, ShardedJedis}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSONObject
import scala.collection.JavaConversions._
import scala.util.parsing.json


object KafkaCounsumer extends SparkStart {

  def getAppName: String = {
    return "KafkaCounsumer"
  }

  private var stream: DStream[(String, String)] = null;
  private var stream_Gis: DStream[(String, String)] = null;
  private var stream_Gis_HD: DStream[(String, String)] = null;

  private var stream_HANDAN_topic: DStream[(String, String)] = null;
  private var stream_ITS_Topic_GIS:DStream[(String, String)] = null;
  private var stream_ITS_Topic_GIS_HD:DStream[(String, String)] = null;
  private var unifiedStream: DStream[(String, String)] = null;

  def doBefor {
    //接收邯郸topic下的流
    stream = kafkaConsumerInputStream(null, Set[String](KafkaConsum.GW2APP.getKey), 1);
    stream_HANDAN_topic = stream.map(x => (KafkaConsum.GW2APP.getKey, x._2))
     var kfkMap:Map[String,String]=Map()
    // var kfkMap:scala.collection.immutable.Map[String,String]=Map()
     kfkMap += ("bootstrap.servers"->"140.143.180.132:26667,140.143.180.132:26668")
     kfkMap += ("key.deserializer"->"kafka.serializer.StringEncoder")
     kfkMap += ("value.deserializer"->"kafka.serializer.StringEncoder")
     kfkMap += ("group.id"->"kafkagroup")
     kfkMap += ("auto.offset.reset"->"largest")
     //接收ITS_Topic_GIS下的流
     stream_Gis=kafkaConsumerInputStream(kfkMap, Set[String](KafkaConsum.ITS_Topic_GIS.getKey), 1)
      stream_ITS_Topic_GIS=stream_Gis.map(x=>(KafkaConsum.ITS_Topic_GIS.getKey, x._2))
     stream_Gis_HD=kafkaConsumerInputStream(kfkMap, Set[String](KafkaConsum.ITS_Topic_GIS_HD.getKey), 1)
      stream_ITS_Topic_GIS_HD=stream_Gis_HD.map(x=>(KafkaConsum.ITS_Topic_GIS_HD.getKey, x._2))


     var kafkaStreams:ListBuffer[DStream[(String, String)]]=ListBuffer[DStream[(String, String)]]()
     kafkaStreams.append(stream_HANDAN_topic)
     kafkaStreams.append(stream_ITS_Topic_GIS)
     kafkaStreams.append(stream_ITS_Topic_GIS_HD)
     unifiedStream=SparkConnection.streamingContext().union(kafkaStreams)
    System.out.println("KafkaCounsumer  --- 启动之前")
  }

  val ebus = new EBUS()

  case class GW2APP(direction: Int, eventTime: String, gprsId: Int, longitude: String, latitude: String, vehicleId: Int)

  def doExecute {
 /*   unifiedStream.map(x=>{
      println(x._1)
     }).print()*/
    val data = new util.HashMap[String, util.Map[String, String]]

    unifiedStream.filter(_._2 != "")
      .map(x => {
        try {
          val topic_name = x._1
          val jon = JSON.parseObject(x._2)

          //读取网关gps数据
          if (topic_name == "gw2app-handan" && jon.get("packetType").toString == "gps") {
            val gw_app = JSON.parseObject(jon.toString, classOf[GW_APP])
            val lineId = gw_app.getGprsId
            val direction = gw_app.getDirection
            val cid = gw_app.getVehicleId
            val start_time = ebus.getStartTimeByLidDir(lineId, direction, gw_app)
            val end_time = ebus.getEndTimeByLidDir(lineId, direction, gw_app)
            if (start_time != "" || end_time != "") (cid + "_" + direction, start_time, end_time, topic_name, jon)
            else ("", "", "", "", "")
            //根据进站包查询
          } else if (topic_name == "gw2app-handan" && jon.get("packetType").toString == "inStation") {
            val inStation = JSON.parseObject(jon.toString, classOf[INSTATION])
            val lineId = inStation.getGprsId
            val direction = inStation.getDirection
            val cid = inStation.getVehicleId
            var start_time = "";
            val end_time = ebus.getEndTimeByLidDir1(lineId, direction, inStation)
            if (inStation.getCurrStationNo == 1) start_time = inStation.getEventTime
            if (start_time != "" || end_time != "") (cid + "_" + direction, start_time, end_time, topic_name, jon)
            else ("", "", "", "", "")
            //根据出站包查询
          } else if (topic_name == "gw2app-handan" && jon.get("packetType").toString == "outStation") {
            val outStation = JSON.parseObject(jon.toString, classOf[OUTSTATION])
            val lineId = outStation.getGprsId
            val direction = outStation.getDirection
            val cid = outStation.getVehicleId
            var start_time = ""
            val end_time = ebus.getEndTimeByLidDir2(lineId, direction, outStation)
            if (outStation.getCurrStationNo == 1) start_time = outStation.getOutStationTime
            if (start_time != "" || end_time != "") (cid + "_" + direction, start_time, end_time, topic_name, jon)
            else ("", "", "", "", "")
            //根据进出场包查询
          } else if (topic_name == "gw2app-handan" && jon.get("packetType").toString == "inoutPark") {
            val inoutPark = JSON.parseObject(jon.toString, classOf[INOUTPARK])
            val cid = inoutPark.getVehicleId
            val direction = inoutPark.getDirection
            var start_time = ""
            var end_time = ""
            if (inoutPark.getFlag == 0) start_time = inoutPark.getEventTime
            if (inoutPark.getFlag == 1) end_time = inoutPark.getEventTime
            if (start_time != "" || end_time != "") (cid + "_" + direction, start_time, end_time, topic_name, jon)
            else ("", "", "", "", "")
          } else if (topic_name == "ITS_Topic_GIS") {
            val jsonGis = JSON.parseObject(jon.get("body").toString)
            val cid = jsonGis.get("dev_id")
            val direction =0 //jsonGis.get("direction").toString.toInt
            val lineId = jsonGis.get("line_id").toString.toInt
            val start_time = ebus.getStartTimeByLidDirG(lineId, direction, jsonGis)
            val end_time = ebus.getEndTimeByLidDir(lineId, direction, jsonGis)
            if (start_time != "" || end_time != "") (cid + "_" + direction, start_time, end_time, topic_name, jsonGis)
            else ("", "", "", "", "")
          } else if (topic_name == "ITS_TOPIC_GIS_HD") {
            val jsonGis_HD = JSON.parseObject(jon.get("body").toString)
            val cid = jsonGis_HD.get("gprsId")
            val direction = jsonGis_HD.get("direction").toString.toInt
            val lineId = jsonGis_HD.get("line_id").toString.toInt
            val start_time = ebus.getStartTimeByLidDirG(lineId, direction, jsonGis_HD)
            val end_time = ebus.getEndTimeByLidDir(lineId, direction, jsonGis_HD)
            if (start_time != "" || end_time != "") (cid + "_" + direction, start_time, end_time, topic_name, jsonGis_HD)
            ("", "", "", "", "")
          }
          else {
            ("", "", "", "", "")
          }
        } catch {
          case e: Exception => e.printStackTrace()
            null
        }
      }).filter(_._1 != "")
      .filter(_._4 != "")
      .foreachRDD(rdd => {

        rdd.foreachPartition(pat => {
          //val jedis=RedisConf.jc
          pat.foreach(pair => {
            val jedis = RedisClient.pool.getResource
            var gpsDataList: util.List[String] = null
            var gpsDataJsonArray: com.alibaba.fastjson.JSONArray = null
            if (pair._4 == "gw2app-handan") {
              val key = getKey(pair._5.toString)
              var routeBusMap = data.get(key)
              if (routeBusMap == null) routeBusMap = new util.HashMap[String, String]()
              val runStatus = JSON.parseObject(pair._5.toString).get("runstatus")
              if (runStatus == 1) {
                if (routeBusMap.containsKey(JSON.parseObject(pair._5.toString).get("vehicleId").toString)) {
                  val gpsDatasJsonStr = routeBusMap(JSON.parseObject(pair._5.toString).get("vehicleId").toString)
                  if (!gpsDatasJsonStr.isEmpty && gpsDatasJsonStr.startsWith("[")) {
                    gpsDataList = JSON.parseArray(gpsDatasJsonStr, classOf[String])
                  }
                } else {
                  gpsDataList = new util.LinkedList[String]
                }
                val lon = JSON.parseObject(pair._5.toString).get("longitude")
                val lat = JSON.parseObject(pair._5.toString).get("latitude")
                val eventTime = JSON.parseObject(pair._5.toString).get("eventTime").toString
                val jo = new com.alibaba.fastjson.JSONObject()
                jo += ("lon" -> lon)
                jo += ("lat" -> lat)
                jo += ("eventTime" ->DateUtil.formatTimeStr2TimeStamp(eventTime))
                gpsDataList.add(0, jo.toString)

                gpsDataJsonArray=ebus.getJSONArray(gpsDataList)

                routeBusMap.put(JSON.parseObject(pair._5.toString).get("vehicleId").toString, gpsDataJsonArray.toJSONString)
                jedis.set(key, routeBusMap.toString)
                println(jedis.get(key))
                data.put(key, routeBusMap)
              }

            } else if (pair._4=="ITS_Topic_GIS" || pair._4=="ITS_Topic_GIS_HD"){
                   val body=JSON.parseObject(pair._5.toString).get("body")
              println(body.toString)
                   val json=JSON.parseObject(body.toString)
                   val runStatus =json.get("vehicle_status")
                   //0x00表示正常，其他表示异常
                   if (runStatus ==0){
                     val key=getITSKey(json.toString)
                     var routeBusMap=data.get(key)
                     if (routeBusMap.isEmpty) routeBusMap = new util.HashMap[String, String]
                     val gpsDatasJsonStr = routeBusMap.get(json.get("dev_id").toString)
                     //if (!gpsDatasJsonStr.isEmpty && gpsDatasJsonStr.startsWith("[")) gpsDataList = json.JSONObject (gpsDatasJsonStr, classOf[String])
                    if (!gpsDatasJsonStr.isEmpty && gpsDatasJsonStr.startsWith("[")) gpsDataList = JSON.parseArray(gpsDatasJsonStr, classOf[String])
                     else gpsDataList = new util.LinkedList[String]

                     val lon=json.get("lon")
                     val lat=json.get("lat")
                     val eventTime=json.get("gps_time").toString
                     var  jo=new com.alibaba.fastjson.JSONObject()
                     jo += ("lon"->lon)
                     jo += ("lat"->lat)
                     jo += ("eventTime"-> DateUtil.formatTimeStr2TimeStamp(eventTime))
                     gpsDataList.add(0, jo.toString)
                     gpsDataJsonArray = new JSONArray
                     gpsDataJsonArray=ebus.getJSONArray(gpsDataList)
                     routeBusMap.put(json.get("dev_id").toString, gpsDataJsonArray.toJSONString)
                     jedis.set(key,routeBusMap.toString)
                     data.put(key,routeBusMap)
                   }
                 }

            var jobj = new com.alibaba.fastjson.JSONObject()
            jobj += ("start_time" -> (if (pair._2 == "") readStime("settle_travltime" + "_130400_" + pair._1, jedis) else DateUtil.formatTimeStr2TimeStamp(pair._2)))
            jobj += ("end_time" -> (if (pair._3 == "") "" else DateUtil.formatTimeStr2TimeStamp(pair._3)))

            jedis.set("settle_travltime" + "_130400_" + pair._1, jobj.toString)
            jedis.expire("settle_travltime" + "_130400_" + pair._1, 24 * 60 * 3600)
            RedisClient.pool.returnResource(jedis)
          })

        })
        //            jedis.close()
      })
  }

  def getKey(str: String): String = {
    val json = JSON.parseObject(str)
    val lineid = json.get("gprsId").toString
    val direction = json.get("direction").toString
    val carId = json.get("vehicleId").toString
    "settle_travl"+"_"+lineid.concat("_").concat(direction).concat("_").concat(carId)
  }

  def getITSKey(str: String): String = {
    val json = JSON.parseObject(str)
    val lineid = json.get("line_id").toString
    val direction = json.get("direction").toString
    val carId = json.get("dev_id").toString
    "settle_travl"+"_"+lineid.concat("_").concat(direction).concat("_").concat(carId)
  }

  def readStime(str: String, jedis: Jedis): String = {
    val result = jedis.get(str)
    if (result != null) JSON.parseObject(result).get("start_time").toString
    else ""
  }

  def readStimeCluster(str: String, jedis: JedisCluster): String = {
    val result = jedis.get(str)
    if (result != null) JSON.parseObject(result).get("start_time").toString
    else ""
  }

  def doAfter {

  }
}
*/
