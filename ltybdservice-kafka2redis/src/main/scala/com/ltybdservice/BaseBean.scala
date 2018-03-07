package com.ltybdservice

import com.alibaba.fastjson.JSON
import com.ltybdservice.config.Param

trait Validate {
  def validate(): Boolean={
    true
  }
}

/**
  * kafka数据格式
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




