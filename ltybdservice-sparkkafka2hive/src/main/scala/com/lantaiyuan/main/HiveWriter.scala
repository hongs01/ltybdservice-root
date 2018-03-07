package com.lantaiyuan.main

import java.text.SimpleDateFormat

import com.alibaba.fastjson.JSON
import com.google.protobuf.TextFormat.ParseException
import org.apache.spark.SparkContext
import org.apache.spark.sql.ForeachWriter
import org.apache.spark.sql.hive.HiveContext


class HiveWriter(sparkContext: SparkContext) extends ForeachWriter[String]{
  
  val spark=new HiveContext(sparkContext)
  val sdf = new SimpleDateFormat("yyyyMM")
  val sd = new SimpleDateFormat("yyyyMMdd")
  val sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")



  def isValidDate(str: String): Boolean = {
    var convertSuccess: Boolean = true
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    try {
      format.setLenient(false)
      format.parse(str)
    } catch {
      case ex: ParseException => convertSuccess = false
    }

    convertSuccess
  }

  override def open(partitionId: Long, version: Long): Boolean = {
//
    true
  }

  override def process(value: String): Unit = {
    val json= JSON.parseObject(value)
    val body= json.getString("body")
    val citycode=json.getString("citycode")
    val jsonstr = JSON.parseObject(body)
    val packetType = jsonstr.getString("packetType")
    if (packetType != null && packetType.equals("gps")) {
      val eventTime = jsonstr.get("eventTime").toString();
      if (eventTime != null && !eventTime.isEmpty) {
        val workmonth = sdf.format(sd1.parse(eventTime))
        val workdate = sd.format(sd1.parse(eventTime))
        if (isValidDate(eventTime)) {
          val azimuth = jsonstr.get("azimuth").toString
          val direction = jsonstr.get("direction").toString
          val gprsId = jsonstr.get("gprsId").toString
          val gpsKm = jsonstr.get("gpsKm").toString
          val height = jsonstr.get("height").toString
          val latitude = jsonstr.get("latitude").toString
          val longitude = jsonstr.get("longitude").toString
          val nextAway = jsonstr.get("nextAway").toString
          val nextStationId = jsonstr.get("nextStationId").toString
          val nextTime = jsonstr.get("nextTime").toString
          val packetType = jsonstr.get("packetType").toString
          val protocolVersion = jsonstr.get("protocolVersion").toString
          val runstatus = jsonstr.get("runstatus").toString
          val signal = jsonstr.get("signal").toString
          val speed = jsonstr.get("speed").toString
          val temp = jsonstr.get("temp").toString
          val vehicleId = jsonstr.get("vehicleId").toString
          val sql = "insert into dc.ods_gps partition(citycode,workmonth,workdate) values( '" + azimuth + "','" + direction + "','" + eventTime + "','" + gprsId + "','" + gpsKm + "','" + height + "','" + latitude + "','" + longitude + "','" + nextAway + "','" + nextStationId + "','" + nextTime + "','" + packetType + "','" + protocolVersion + "','" + runstatus + "','" + signal + "','" + speed + "','" + temp + "','" + vehicleId + "','" + citycode + "','" + workmonth + "','" + workdate + "')"
          println(sql)
          spark.sql("set hive.exec.dynamic.partition=true")
          spark.sql("set hive.exec.dynamic.partition.mode=nostrick")
          spark.sql(sql)
        }

      }
    }
    if (packetType != null && packetType.equals("inStation")) {
      val eventTime = jsonstr.get("eventTime").toString();
      if (eventTime != null && !eventTime.isEmpty) {
        val workmonth = sdf.format(sd1.parse(eventTime))
        val workdate = sd.format(sd1.parse(eventTime))
        if (isValidDate(eventTime)) {
          val announcerType = jsonstr.get("announcerType").toString
          val autoSpeakerCode = jsonstr.get("autoSpeakerCode").toString
          val currStationNo = jsonstr.get("currStationNo").toString
          val direction = jsonstr.get("direction").toString
          val gprsId = jsonstr.get("gprsId").toString
          val packetType = jsonstr.get("packetType").toString
          val protocolVersion = jsonstr.get("protocolVersion").toString
          val runstatus = jsonstr.get("runstatus").toString
          val stationIdentify = jsonstr.get("stationIdentify").toString
          val vehicleId = jsonstr.get("vehicleId").toString
          val sql = "insert into dc.ods_instation partition(citycode,workmonth,workdate) values( '" + announcerType + "','" + autoSpeakerCode + "','" + currStationNo + "','" + direction + "','" + eventTime + "','" + gprsId + "','" + packetType + "','" + protocolVersion + "','" + runstatus + "','" + stationIdentify + "','" + vehicleId + "','" + citycode + "','" + workmonth + "','" + workdate + "')"
          println(sql)
          spark.sql("set hive.exec.dynamic.partition=true")
          spark.sql("set hive.exec.dynamic.partition.mode=nostrick")
          spark.sql(sql)
        }

      }
    }
    if (packetType != null && packetType.equals("outStation")) {
      val inStationTime = jsonstr.get("inStationTime").toString();
      if (inStationTime != null && !inStationTime.isEmpty) {
        val workmonth = sdf.format(sd1.parse(inStationTime))
        val workdate = sd.format(sd1.parse(inStationTime))
        if (isValidDate(inStationTime)) {
          val announcerType = jsonstr.get("announcerType").toString
          val autoSpeakerCode = jsonstr.get("autoSpeakerCode").toString
          val currStationNo = jsonstr.get("currStationNo").toString
          val direction = jsonstr.get("direction").toString
          val driverCode = jsonstr.get("driverCode").toString
          val gprsId = jsonstr.get("gprsId").toString
          val inStationTime = jsonstr.get("inStationTime").toString
          val outStationTime = jsonstr.get("outStationTime").toString
          val packetType = jsonstr.get("packetType").toString
          val protocolVersion = jsonstr.get("protocolVersion").toString
          val runstatus = jsonstr.get("runstatus").toString
          val stationIdentify = jsonstr.get("stationIdentify").toString
          val vehicleId = jsonstr.get("vehicleId").toString
          val sql = "insert into dc.ods_outstation partition(citycode,workmonth,workdate) values( '" + announcerType + "','" + autoSpeakerCode + "','" + currStationNo + "','" + direction + "','" + driverCode + "','" + gprsId + "','" + inStationTime + "','" + outStationTime + "','" + packetType + "','" + protocolVersion + "','" + runstatus + "','" + stationIdentify + "','" + vehicleId + "','" + citycode + "','" + workmonth + "','" + workdate + "')"
          println(sql)
          spark.sql("set hive.exec.dynamic.partition=true")
          spark.sql("set hive.exec.dynamic.partition.mode=nostrick")
          spark.sql(sql)
        }

      }
    }
    if (packetType != null && packetType.equals("inoutPark")) {
      val eventTime = jsonstr.get("eventTime").toString();
      if (eventTime != null && !eventTime.isEmpty) {
        val workmonth = sdf.format(sd1.parse(eventTime))
        val workdate = sd.format(sd1.parse(eventTime))
        if (isValidDate(eventTime)) {
          val azimuth = jsonstr.get("azimuth").toString
          val battery = jsonstr.get("battery").toString
          val direction = jsonstr.get("direction").toString
          val flag = jsonstr.get("flag").toString
          val gas = jsonstr.get("gas").toString
          val gprsId = jsonstr.get("gprsId").toString
          val latitude = jsonstr.get("latitude").toString
          val longitude = jsonstr.get("longitude").toString
          val packingLotNo = jsonstr.get("packingLotNo").toString
          val plProperty = jsonstr.get("plProperty").toString
          val protocolVersion = jsonstr.get("protocolVersion").toString
          val runstatus = jsonstr.get("runstatus").toString
          val speed = jsonstr.get("speed").toString
          val vehicleId = jsonstr.get("vehicleId").toString
          val sql = "insert into dc.ods_inoutpark partition(citycode,workmonth,workdate) values( '" + azimuth + "','" + battery + "','" + direction + "','" + eventTime + "','" + flag + "','" + gas + "','" + gprsId + "','" + latitude + "','" + longitude + "','" + packetType + "','" + packingLotNo + "','" + plProperty +  "','" + protocolVersion +  "','" + runstatus + "','" + speed + "','" + vehicleId +  "','" + citycode + "','" + workmonth + "','" + workdate + "')"
          println(sql)
          spark.sql("set hive.exec.dynamic.partition=true")
          spark.sql("set hive.exec.dynamic.partition.mode=nostrick")
          spark.sql(sql)
        }

      }
    }
  }

  override def close(errorOrNull: Throwable): Unit = {
    
  }
}


