package com.lantaiyuan.common

import com.alibaba.fastjson.JSON
import com.ltybdservice.config.Param

trait Validate {
  def validate(): Boolean={
    true
  }
}

/**
  * {"azimuth":188,"direction":0,"eventTime":"2017-12-11 11:07:13","gprsId":621,"gpsKm":46948,"height":0,
  * "latitude":"36.5727215","longitude":"114.47330483333333","nextAway":0,"nextStationId":2,"nextTime":0,
  * "packetType":"gps","protocolVersion":"HDBS","runstatus":129,"signal":31,"speed":0,"temp":0,"vehicleId":15144}
  */

case class Bus(cityCode:String,vehicleId:Int)extends Validate{
  override def validate(): Boolean={
    if(cityCode.isEmpty||vehicleId.isNaN){
      false
    }else {
      true
    }
  }
}
case class BusRealTime(eventTime: String,longitude:Double,latitude:Double)extends Validate{
  override def validate(): Boolean={
    if(eventTime.isEmpty||longitude.isNaN||latitude.isNaN){
      false
    }else {
      true
    }
  }
  def mkString():String ={
    "{\"eventTime\":"+ "\"" + eventTime+ "\""+","+ "\""+"longitude\":"+longitude+","+ "\""+ "latitude\":"+latitude+"}"
  }
}
case class BusInfo(bus:Bus,busRealTime: BusRealTime)extends Validate{
  override def validate(): Boolean={
    if(bus.validate()&&busRealTime.validate()){
      true
    }else {
      false
    }
  }
}
object BusInfo{
  lazy val cityCode= Param.param.getCityCode
  def getBusInfo(str:String):BusInfo={
    val obj =JSON.parseObject(str)
    val bus = Bus(cityCode,obj.getIntValue("vehicleId"))
    val busRealTime = BusRealTime(obj.getString("eventTime"),obj.getDouble("longitude"),obj.getDouble("latitude"))
    BusInfo(bus,busRealTime)
  }
}


case class Location(longitude: Double, latitude: Double) extends Validate{
  override def validate() = {
    if(longitude.toString.isEmpty||latitude.toString.isEmpty){
      false
    }else{
      true
    }
  }
}

case class bus_gps(azimuth:String,direction:String,eventtime:String,gprsid:String,gpskm:String,height:String,latitude:String,longitude:String,nextaway:String,nextstationid:String,nexttime:String,packettype:String,protocolversion:String,runstatus:String,signal:String,speed:String,temp:String,vehicleid:String,citycode:String,workmonth:String,workdate:String) extends Validate{
  override def validate() = {
    true
  }
}

case class bus_inStation(announcerType:String,autoSpeakerCode:String,currStationNo:String,direction:String,eventTime:String,gprsId:String,packetType:String,protocolVersion:String,runstatus:String,stationIdentify:String,vehicleId:String,citycode:String,workmonth:String,workdate:String) extends Validate{
  override def validate() = {
    true
  }
}

case class bus_outStation(announcerType :String, autoSpeakerCode :String, currStationNo :String, direction :String, driverCode :String, gprsId :String, inStationTime :String, outStationTime :String, packetType :String, protocolVersion :String, runstatus :String, stationIdentify :String, vehicleId :String, citycode :String, workmonth :String, workdate:String) extends Validate{
  override def validate() = {
    true
  }
}

case class bus_inoutPark(azimuth :String, battery :String, direction :String, eventTime :String, flag :String, gas :String, gprsId :String, latitude :String, longitude :String, packetType :String, packingLotNo :String, plProperty :String,protocolVersion :String, runstatus :String, speed :String, vehicleId :String, citycode :String, workmonth :String, workdate:String) extends Validate{
  override def validate() = {
    true
  }
}

object main extends App{
  println(BusRealTime("asd",0.1,0.1).mkString())
}





