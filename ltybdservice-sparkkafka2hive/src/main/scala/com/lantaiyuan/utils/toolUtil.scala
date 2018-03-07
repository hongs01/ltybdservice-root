package com.lantaiyuan.utils

import java.text.{ParseException, SimpleDateFormat}

import com.alibaba.fastjson.{JSON, JSONException}
import com.lantaiyuan.common.{bus_gps, bus_inStation, bus_inoutPark, bus_outStation}
import org.apache.spark.sql.Row

object toolUtil {

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

  def getPacketGPS(row:Any):Boolean= {
    var re = true
    try {
      val json = JSON.parseObject(row.toString)
      val packetType = json.get("packetType")
      if (packetType != null && packetType.equals("gps")) {
        val eventTime = json.get("eventTime").toString();
        if(eventTime !=null && isValidDate(eventTime))
          re = true
        else
          re = false
      } else
        re = false
    }catch {
      case e: JSONException=>re=false
    }
    re
  }

  def getPacketIN(row:Any):Boolean= {
    var re = true
    try {
      val json = JSON.parseObject(row.toString)
      val packetType = json.get("packetType")
      if (packetType != null && packetType.equals("inStation")) {
        val eventTime = json.get("eventTime").toString();
        if(eventTime !=null && isValidDate(eventTime))
          re = true
        else
          re = false
      } else
        re = false
    }catch {
      case e: JSONException=>re=false
    }
    re
  }

  def getPacketOUT(row:Any):Boolean= {
    var re = true
    try {
      val json = JSON.parseObject(row.toString)
      val packetType = json.get("packetType")
      if (packetType != null && packetType.equals("outStation")) {
        val inStationTime = json.get("inStationTime").toString();
        if(inStationTime !=null && isValidDate(inStationTime))
          re = true
        else
          re = false
      } else
        re = false
    }catch {
      case e: JSONException=>re=false
    }
    re
  }

  def getPacketinoutPark(row:Any):Boolean= {
    var re = true
    try {
      val json = JSON.parseObject(row.toString)
      val packetType = json.get("packetType")
      if (packetType != null && packetType.equals("inoutPark")) {
        val eventTime = json.get("eventTime").toString();
        if(eventTime !=null && isValidDate(eventTime))
          re = true
        else
          re = false
      } else
        re = false
    }catch {
      case e: JSONException=>re=false
    }
    re
  }

  def anyToString(obj:AnyRef):String={
    if(obj==null)
      ""
    else
      obj.toString
    
  }

  def getBusGps(citycode:String,row:String):bus_gps={
    val jsonstr = JSON.parseObject(row)
    val packetType = jsonstr.getString("packetType")
    val eventTime = jsonstr.get("eventTime").toString();
    val workmonth = sdf.format(sd1.parse(eventTime))
    val workdate = sd.format(sd1.parse(eventTime))
    println(workdate)
    val azimuth = anyToString(jsonstr.get("azimuth"))
    val direction = anyToString(jsonstr.get("direction"))
    val gprsId = anyToString(jsonstr.get("gprsId"))
    val gpsKm = anyToString(jsonstr.get("gpsKm"))
    val height = anyToString(jsonstr.get("height"))
    val latitude = anyToString(jsonstr.get("latitude"))
    val longitude = anyToString(jsonstr.get("longitude"))
    val nextAway = anyToString(jsonstr.get("nextAway"))
    val nextStationId = anyToString(jsonstr.get("nextStationId"))
    val nextTime = anyToString(jsonstr.get("nextTime"))
    val protocolVersion = anyToString(jsonstr.get("protocolVersion"))
    val runstatus = anyToString(jsonstr.get("runstatus"))
    val signal = anyToString(jsonstr.get("signal"))
    val speed = anyToString(jsonstr.get("speed"))
    val temp = anyToString(jsonstr.get("temp"))
    val vehicleId = anyToString(jsonstr.get("vehicleId"))
    bus_gps(azimuth,direction,eventTime,gprsId,gpsKm,height,latitude,longitude,nextAway,nextStationId,nextTime,packetType,protocolVersion,runstatus,signal,speed,temp,vehicleId,citycode,workmonth,workdate)

  }

  def getBusIN(citycode:String,row:String):bus_inStation={
    val jsonstr = JSON.parseObject(row)
    val eventTime = jsonstr.get("eventTime").toString();
    val workmonth = sdf.format(sd1.parse(eventTime))
    val workdate = sd.format(sd1.parse(eventTime))
    val announcerType = anyToString(jsonstr.get("announcerType"))
    val autoSpeakerCode = anyToString(jsonstr.get("autoSpeakerCode"))
    val currStationNo = anyToString(jsonstr.get("currStationNo"))
    val direction = anyToString(jsonstr.get("direction"))
    val gprsId = anyToString(jsonstr.get("gprsId"))
    val packetType = anyToString(jsonstr.get("packetType"))
    val protocolVersion = anyToString(jsonstr.get("protocolVersion"))
    val runstatus = anyToString(jsonstr.get("runstatus"))
    val stationIdentify = anyToString(jsonstr.get("stationIdentify"))
    val vehicleId = anyToString(jsonstr.get("vehicleId"))
    bus_inStation(announcerType,autoSpeakerCode,currStationNo,direction,eventTime,gprsId,packetType,protocolVersion,runstatus,stationIdentify,vehicleId,citycode,workmonth,workdate)
  }

  def getBusOUT(citycode:String,row:String):bus_outStation={
    val jsonstr = JSON.parseObject(row)
    val inStationTime = jsonstr.get("inStationTime").toString();
    val workmonth = sdf.format(sd1.parse(inStationTime))
    val workdate = sd.format(sd1.parse(inStationTime))
    val announcerType = anyToString(jsonstr.get("announcerType"))
    val autoSpeakerCode = anyToString(jsonstr.get("autoSpeakerCode"))
    val currStationNo = anyToString(jsonstr.get("currStationNo"))
    val direction = anyToString(jsonstr.get("direction"))
    val driverCode = anyToString(jsonstr.get("driverCode"))
    val outStationTime = anyToString(jsonstr.get("outStationTime"))
    val gprsId = anyToString(jsonstr.get("gprsId"))
    val packetType = anyToString(jsonstr.get("packetType"))
    val protocolVersion = anyToString(jsonstr.get("protocolVersion"))
    val runstatus = anyToString(jsonstr.get("runstatus"))
    val stationIdentify = anyToString(jsonstr.get("stationIdentify"))
    val vehicleId = anyToString(jsonstr.get("vehicleId"))
    bus_outStation(announcerType, autoSpeakerCode, currStationNo, direction, driverCode, gprsId, inStationTime, outStationTime, packetType, protocolVersion, runstatus, stationIdentify, vehicleId, citycode, workmonth, workdate)
  }

  def getBusinoutPark(citycode:String,row:String):bus_inoutPark={
    val jsonstr = JSON.parseObject(row)
    val eventTime = jsonstr.get("eventTime").toString();
    val workmonth = sdf.format(sd1.parse(eventTime))
    val workdate = sd.format(sd1.parse(eventTime))
    val azimuth = anyToString(jsonstr.get("azimuth"))
    val battery = anyToString(jsonstr.get("battery"))
    val flag = anyToString(jsonstr.get("flag"))
    val direction = anyToString(jsonstr.get("direction"))
    val gas = anyToString(jsonstr.get("gas"))
    val latitude = anyToString(jsonstr.get("latitude"))
    val longitude = anyToString(jsonstr.get("longitude"))
    val gprsId = anyToString(jsonstr.get("gprsId"))
    val packetType = anyToString(jsonstr.get("packetType"))
    val packingLotNo = anyToString(jsonstr.get("packingLotNo"))
    val plProperty = anyToString(jsonstr.get("plProperty"))
    val protocolVersion = anyToString(jsonstr.get("protocolVersion"))
    val runstatus = anyToString(jsonstr.get("runstatus"))
    val speed = anyToString(jsonstr.get("speed"))
    val vehicleId = anyToString(jsonstr.get("vehicleId"))
    bus_inoutPark(azimuth, battery, direction, eventTime, flag, gas, gprsId, latitude, longitude, packetType, packingLotNo, plProperty,protocolVersion, runstatus, speed, vehicleId, citycode, workmonth, workdate)
  }

  def getBusGps(row:Row):bus_gps={
    val citycode=CitycodeUtil.getCitycode(row(0).toString)
    val jsonstr = JSON.parseObject(row(1).toString)
    val packetType = jsonstr.getString("packetType")
    val eventTime = jsonstr.get("eventTime").toString();
    val workmonth = sdf.format(sd1.parse(eventTime))
    val workdate = sd.format(sd1.parse(eventTime))
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
    val protocolVersion = jsonstr.get("protocolVersion").toString
    val runstatus = jsonstr.get("runstatus").toString
    val signal = jsonstr.get("signal").toString
    val speed = jsonstr.get("speed").toString
    val temp = jsonstr.get("temp").toString
    val vehicleId = jsonstr.get("vehicleId").toString
    bus_gps(azimuth,direction,eventTime,gprsId,gpsKm,height,latitude,longitude,nextAway,nextStationId,nextTime,packetType,protocolVersion,runstatus,signal,speed,temp,vehicleId,citycode,workmonth,workdate)

  }

}
