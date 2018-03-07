package com.ltybdservice.caseclass

import com.alibaba.fastjson.JSON
import com.ltybdservice.util.{DateChangeUtil, FormatUtil}

trait Validate {
  def validate(): Boolean={
    true
  }
}

case class GpsInfo(rowKey:String,
                   devSn:String,
                   devId:String,
                   cityCode:String,
                   companyCode:String,
                   lineId:String,
                   gpsTime:String,
                   lon:String,
                   lat:String,
                   angle:String,
                   altitude:String,
                   speed:String,
                   distance:String,
                   disNext:String,
                   timeNext:String,
                   nextStationNo:String,
                   vehicleStatus:String) extends Validate {
  override def validate(): Boolean={
    if(rowKey.isEmpty){
      false
    }else {
      true
    }
  }
}

object GpsInfo{
  def getGpsInfo(str:String):GpsInfo={
    val obj = JSON.parseObject(str)
    val body = JSON.parseObject(obj.getString("body"))


    val rowKey = FormatUtil.ROW_KEY_COMMON_FORMAT.format(body.getString("line_id").toInt,
      body.getString("dev_id").toInt,
      DateChangeUtil.DateFormat(body.getString("gps_time"),FormatUtil.DATE_FORMAT_YYYYMMDDHHMMSS))

//    println(rowkey)

    GpsInfo(rowKey,
      body.getString("dev_sn"),
      body.getString("dev_id"),
      body.getString("city_code"),
      body.getString("company_code"),
      body.getString("line_id"),
      body.getString("gps_time"),
      body.getString("lon"),
      body.getString("lat"),
      body.getString("angle"),
      body.getString("altitude"),
      body.getString("speed"),
      body.getString("distance"),
      body.getString("dis_next"),
      body.getString("time_next"),
      body.getString("next_station_no"),
      body.getString("vehicle_status"))
  }
}