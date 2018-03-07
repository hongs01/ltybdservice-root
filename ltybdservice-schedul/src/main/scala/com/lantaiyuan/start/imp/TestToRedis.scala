/*
package com.lantaiyuan.start.imp

import java.util

import com.alibaba.fastjson.{JSON, JSONObject}
import com.lantaiyuan.bean._
import com.lantaiyuan.common.constEnum.KafkaConsum
import com.lantaiyuan.start.SparkStart
import com.lantaiyuan.staticCache.imp.EBUS
import com.lantaiyuan.utils.{DateUtil, RedisConf}
import org.apache.spark.streaming.dstream.DStream
import redis.clients.jedis.{Jedis, JedisCluster, ShardedJedis}

import scala.collection.JavaConversions._

object TestToRedis /*extends SparkStart */{

  def getAppName: String = {
    return "TestToRedis"
  }

  private var stream: DStream[(String, String)] = null;
  private var stream1:DStream[(String,String)] =null;

  def doBefor {
    stream =null //kafkaConsumerInputStream(null, Set[String](KafkaConsum.GW2APP.getKey), 1);
   /* var kfkMap:Map[String,String]=Map()
    kfkMap += ("bootstrap.servers"->"10.1.254.11:6667")
    kfkMap += ("key.deserializer"->"kafka.serializer.StringEncoder")
    kfkMap += ("value.deserializer"->"kafka.serializer.StringEncoder")
    kfkMap += ("group.id"->"kafkagroup")
    kfkMap += ("auto.offset.reset"->"largest")

    stream1=kafkaConsumerInputStream(kfkMap, Set[String]("kktest"), 1);*/
    System.out.println("TestToRedis  --- 启动之前")
  }

  val ebus = new EBUS()

  case class GW2APP(direction: Int, eventTime: String, gprsId: Int, longitude: String, latitude: String, vehicleId: Int)

  def doExecute {
   /* stream1.map(x=>{
      val json=JSON.parseObject(x._2)
      println(json.get("header"))
    })*/
    stream.filter(_._2 != "")
      .map(x => {

        try {
          val topic_name=x._1
          val jon = JSON.parseObject(x._2)
          //读取网关gps数据
          if (jon.get("packetType").toString == "gps") {
            val gw_app = JSON.parseObject(jon.toString, classOf[GW_APP])
            val lineId = gw_app.getGprsId
            val direction = gw_app.getDirection
            val cid = gw_app.getVehicleId
            val start_time = ebus.getStartTimeByLidDir(lineId, direction, gw_app)
            val end_time = ebus.getEndTimeByLidDir(lineId, direction, gw_app)
            if (start_time != "" || end_time != "") (cid + "_" + direction, start_time, end_time)
            else("", "", "")
                //根据进站包查询
          } else if(jon.get("packetType").toString == "inStation") {
            val inStation = JSON.parseObject(jon.toString, classOf[INSTATION])
            val lineId = inStation.getGprsId
            val direction = inStation.getDirection
            val cid = inStation.getVehicleId
            var start_time = "";
            val end_time = ebus.getEndTimeByLidDir1(lineId, direction, inStation)
            if (inStation.getCurrStationNo == 1) start_time = inStation.getEventTime
            if (start_time != "" || end_time != "")  (cid + "_" + direction, start_time, end_time)
            else("", "", "")
                   //根据出站包查询
          }else if(jon.get("packetType").toString == "outStation"){
            val outStation = JSON.parseObject(jon.toString, classOf[OUTSTATION])
            val lineId = outStation.getGprsId
            val direction = outStation.getDirection
            val cid = outStation.getVehicleId
            var start_time = "";
            val end_time = ebus.getEndTimeByLidDir2(lineId, direction, outStation)
            if (outStation.getCurrStationNo == 1) start_time = outStation.getOutStationTime
            if (start_time != "" || end_time != "") (cid + "_" + direction, start_time, end_time)
            else("", "", "")
                      //根据进出场包查询
          }else if(jon.get("packetType").toString == "inoutPark"){
            val inoutPark = JSON.parseObject(jon.toString, classOf[INOUTPARK])
            val cid = inoutPark.getVehicleId
            val direction = inoutPark.getDirection
            var start_time = ""
            var end_time = ""
            if (inoutPark.getFlag == 0) start_time = inoutPark.getEventTime
            if (inoutPark.getFlag == 1) end_time = inoutPark.getEventTime
            if (start_time != "" || end_time != "") (cid + "_" + direction, start_time, end_time)
            else("", "", "")
          }else if(topic_name.startsWith("ITS_Topic_GIS") && jon.get("msg_sn")=="0"){
            ("","","")
          }
          else {
            ("", "", "")
          }
        } catch {
          case e: Exception => e.printStackTrace()
            null
        }
      }).filter(_._1!="")
          .foreachRDD(rdd=>{

             rdd.foreachPartition(pat=>{
               //val jedis=RedisConf.jc
               pat.foreach(pair=>{
                 val jedis = RedisClient.pool.getResource

                 var jobj:JSONObject=new JSONObject()
                 jobj += ("start_time"->(if(pair._2=="") readStime("settle_travltime"+"_130400_"+pair._1,jedis) else DateUtil.formatTimeStr2TimeStamp(pair._2)))
                 jobj += ("end_time"->(if(pair._3=="") "" else DateUtil.formatTimeStr2TimeStamp(pair._3)))

                 jedis.set("settle_travltime"+"_130400_"+pair._1,jobj.toString)
                 jedis.expire("settle_travltime"+"_130400_"+pair._1,24*60*3600)
                 RedisClient.pool.returnResource(jedis)

                 println("jedis:"+jedis.get("settle_travltime"+"_130400_"+pair._1))
               })
//
             })
//            jedis.close()
          })
  }

  def readStime(str:String ,jedis: Jedis):String={
    val result=jedis.get(str)
    if(result!=null)  JSON.parseObject(result).get("start_time").toString
    else   ""
  }

  def readStimeCluster(str:String ,jedis: JedisCluster):String={
    val result=jedis.get(str)
    if(result!=null)  JSON.parseObject(result).get("start_time").toString
    else   ""
  }

  def doAfter {

  }
}
*/
