package com.ltybdservice.v2

import com.alibaba.fastjson.JSON
import com.ltybdservice.commonutil.DateUtil
import com.ltybdservice.config.Param

trait Validate {
  def validate(): Boolean={
    true
  }
}


case class Bus2(cityCode:String,vehicleId:Int)extends Validate{
  override def validate(): Boolean={
    if(cityCode.isEmpty||vehicleId.isNaN){
      false
    }else {
      true
    }
  }
}
case class BusRealTime2(eventTime: String,lineId:Int,direction:Int,longitude:Double,latitude:Double)extends Validate{
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
case class BusInfo2(bus:Bus2,busRealTime: BusRealTime2,infoType:Int)extends Validate{
  override def validate(): Boolean={
    if(bus.validate()&&busRealTime.validate()){
      true
    }else {
      false
    }
  }
}

/**
新网关进出站数据格式
ITS_Topic_StationIO
{
    "header": {
        "msg_sn": 0,
        "msg_id": 257
    },
    "body": {
        "dev_sn": "",
        "dev_id": 3160,
        "city_code": "",
        "company_code": "76",
        "line_id": 351,
        "in_time": 1516179333,
        "bus_station_no": 24,
        "station_flag": 85,
        "station_report": 1,
        "dis_next": 0,
        "time_next": 0,
        "vehicle_status": 0
    }
}
新网关实时gps数据格式
ITS_Topic_GIS
ITS_Topic_GIS_HD
{
    "header": {
        "msg_sn": 13913,
        "msg_id": 259
    },
    "body": {
        "dev_sn": "L442b02cf78ff7a",
        "dev_id": 15443,
        "city_code": "130400",
        "company_code": "01",
        "line_id": 781,
        "gps_time": 1517561117,
        "lon": 114.327,
        "lat": 36.686584,
        "angle": 101,
        "altitude": 0,
        "speed": 0,
        "distance": 148119,
        "direction": 1,
        "dis_next": 0,
        "time_next": 0,
        "next_station_no": 2,
        "vehicle_status": 1
    }
}
老网关进出场数据格式
{
    "azimuth": 168,
    "battery": 0,
    "direction": 1,
    "eventTime": "2018-02-02 17:26:17",
    "flag": 1,
    "gas": 0,
    "gprsId": 61,
    "latitude": "36.6275565",
    "longitude": "114.4798615",
    "packetType": "inoutPark",
    "packingLotNo": 118,
    "plProperty": 0,
    "protocolVersion": "HDBS",
    "runstatus": 131,
    "speed": 13,
    "vehicleId": 1066
}
老网关实时gps数据格式
{
    "azimuth": 302,
    "direction": 1,
    "eventTime": "2018-02-02 17:22:46",
    "gprsId": 281,
    "gpsKm": 201522,
    "height": 0,
    "latitude": "36.45811166666667",
    "longitude": "114.67322333333334",
    "nextAway": 18,
    "nextStationId": 10,
    "nextTime": 104,
    "packetType": "gps",
    "protocolVersion": "HDBS",
    "runstatus": 3,
    "signal": 23,
    "speed": 15,
    "temp": 25,
    "vehicleId": 1077
}
老网关进站数据格式
{
    "announcerType": 0,
    "autoSpeakerCode": 0,
    "currStationNo": 23,
    "direction": 1,
    "eventTime": "2018-02-02 17:26:17",
    "gprsId": 261,
    "packetType": "inStation",
    "protocolVersion": "HDBS",
    "runstatus": 3,
    "stationIdentify": 85,
    "vehicleId": 17177
}
老网关出站数据格式
{
    "announcerType": 0,
    "autoSpeakerCode": 0,
    "currStationNo": 11,
    "direction": 1,
    "driverCode": 4353,
    "gprsId": 311,
    "inStationTime": "2018-02-02 17:22:05",
    "outStationTime": "2018-02-02 17:22:47",
    "packetType": "outStation",
    "protocolVersion": "HDBS",
    "runstatus": 3,
    "stationIdentify": 1,
    "vehicleId": 15020
}
  */
//{"azimuth":168,"battery":0,"direction":1,"eventTime":"2018-02-02 17:26:17","flag":1,"gas":0,"gprsId":61,"latitude":"36.6275565","longitude":"114.4798615","packetType":"inoutPark","packingLotNo":118,"plProperty":0,"protocolVersion":"HDBS","runstatus":131,"speed":13,"vehicleId":1066}
//{"azimuth":302,"direction":1,"eventTime":"2018-02-02 17:22:46","gprsId":281,"gpsKm":201522,"height":0,"latitude":"36.45811166666667","longitude":"114.67322333333334","nextAway":18,"nextStationId":10,"nextTime":104,"packetType":"gps","protocolVersion":"HDBS","runstatus":3,"signal":23,"speed":15,"temp":25,"vehicleId":1077}
//{"announcerType":0,"autoSpeakerCode":0,"currStationNo":11,"direction":1,"driverCode":4353,"gprsId":311,"inStationTime":"2018-02-02 17:22:05","outStationTime":"2018-02-02 17:22:47","packetType":"outStation","protocolVersion":"HDBS","runstatus":3,"stationIdentify":1,"vehicleId":15020}
//{"announcerType":0,"autoSpeakerCode":0,"currStationNo":23,"direction":1,"eventTime":"2018-02-02 17:26:17","gprsId":261,"packetType":"inStation","protocolVersion":"HDBS","runstatus":3,"stationIdentify":85,"vehicleId":17177}
//怀疑新网关也是分进出站有不同数据格式的，对应in_time，有out_time
object BusInfo2{
  lazy val confCityCode= Param.param.getCityCode
  def getBusInfo(str:String):BusInfo2={
    //新网关数据
    if(str.contains("header")){
      val obj =JSON.parseObject(str)
      val header=obj.getJSONObject("header")
      val msgId=header.getIntValue("msg_id")
      val body=obj.getJSONObject("body")
      //如果city_code字段值为空则设置为配置的值
      val cityCode=if("".equals(body.getString("city_code"))){
        body.getString("city_code")
      }else{
        confCityCode
      }
      //实时gps数据，ITS_Topic_GIS,ITS_Topic_GIS_HD
      if(msgId.equals(259)){
        val bus = Bus2(cityCode,body.getIntValue("dev_id"))
        val time=body.getLongValue("gps_time")
        val busRealTime = BusRealTime2(getTime(time),body.getIntValue("line_id"),body.getIntValue("direction"),body.getDouble("lon"),body.getDouble("lat"))
        BusInfo2(bus,busRealTime,InfoType.ITS_Topic_GIS)
      }
      //进出站数据，ITS_Topic_StationIO
      else if(msgId.equals(257)){
        val bus = Bus2(cityCode,body.getIntValue("dev_id"))
        val time=body.getLongValue("in_time")
        val busRealTime = BusRealTime2(getTime(time),body.getIntValue("line_id"),0,0d,0d)
        BusInfo2(bus,busRealTime,InfoType.ITS_Topic_StationIO)
      }
      else{
        BusInfo2(Bus2("",0),BusRealTime2("",0,0,0d,0d),InfoType.OTHER)
      }
    }
    //老网关数据
    else{
      val obj =JSON.parseObject(str)
      val bus = Bus2(confCityCode,obj.getIntValue("vehicleId"))
      val packetType=obj.getString("packetType")
      //进出场
      if("inoutPark".equals(packetType)){
        val busRealTime = BusRealTime2(obj.getString("eventTime"),obj.getIntValue("gprsId"),obj.getIntValue("direction"),obj.getDouble("longitude"),obj.getDouble("latitude"))
        BusInfo2(bus,busRealTime,InfoType.InOutPark)
      }
      //gps实时信息
      else if("gps".equals(packetType)){
        val busRealTime = BusRealTime2(obj.getString("eventTime"),obj.getIntValue("gprsId"),obj.getIntValue("direction"),obj.getDouble("longitude"),obj.getDouble("latitude"))
        BusInfo2(bus,busRealTime,InfoType.GPS)
      }
      //进站
      else if("inStation".equals(packetType)){
        val busRealTime = BusRealTime2(obj.getString("eventTime"),obj.getIntValue("gprsId"),obj.getIntValue("direction"),0d,0d)
        BusInfo2(bus,busRealTime,InfoType.InStation)
      }
      //出站
      else if("outStation".equals(packetType)){
        val busRealTime = BusRealTime2(obj.getString("outStationTime"),obj.getIntValue("gprsId"),obj.getIntValue("direction"),0d,0d)
        BusInfo2(bus,busRealTime,InfoType.OutStation)
      }
      else {
        BusInfo2(Bus2("",0),BusRealTime2("",0,0,0d,0d),InfoType.OTHER)
      }
    }



  }
  def getTime(time:Long): String ={
    if(0.equals(time)){
      ""
    }else{
      DateUtil.stamp10toDateString(time)
    }
  }
}






