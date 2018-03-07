package com.ltybdservice

import com.ltybdservice.geohashutil.GeoHash
import com.ltybdservice.redisutil.RedisClusterConf
import redis.clients.jedis.GeoUnit
import redis.clients.jedis.params.geo.GeoRadiusParam

trait Validate {
  def validate(): Boolean={
    true
  }
}

/**
  * 用户基本信息
  * @param userId
  * @param phoneModel
  * @param cityCode
  */
case class User(userId: String, phoneModel:String, cityCode: String)extends Validate{
  override def validate() = {
    if((userId.isEmpty&&(phoneModel.isEmpty))||cityCode.isEmpty){
      false
    }else{
      true
    }
  }
}

/**
  * 用户原始信息
  * @param userId
  * @param phoneModel
  * @param cityCode
  * @param longitude
  * @param latitude
  * @param currentTime
  * @param region
  */
case class RawUserRealTime(userId: String, phoneModel:String, cityCode: String, longitude: Double, latitude: Double, currentTime: java.sql.Timestamp, region:String) extends Validate{
  override def validate() = {
    if((userId.isEmpty&&(phoneModel.isEmpty))||cityCode.isEmpty){
      false
    }else{
      true
    }
  }
}

/**
  * 区域包含一个location表示坐标，一个string以字符串表示该区域，如gohash编码
  * @param location
  * @param string
  */
case  class Region(location: Location,string:String) extends Validate{
  override def validate() = {
    if(!location.validate()){
      false
    }else{
      true
    }
  }
  override def toString(): String = {
    string
  }
}

/**
  * 经纬度包装
  * @param longitude
  * @param latitude
  */
case class Location(longitude: Double, latitude: Double) extends Validate{
  override def validate() = {
    if(longitude.toString.isEmpty||latitude.toString.isEmpty){
      false
    }else{
      true
    }
  }
}

/**
  * 站点基本信息
  * @param stationId
  * @param cityCode
  * @param location
  */
case class Station(stationId: String, cityCode: String, location: Location)extends Validate{
  override def validate() = {
    if(stationId.isEmpty||cityCode.isEmpty||(!location.validate())){
      false
    }else{
      true
    }
  }
  def getLocation(): Location = {
    location
  }
}
object Station{
  /**
    *
    * @param userRealTime
    * @param km
    * @return
    * 可能在附近范围内找不到站点，因此返回一个Option
    */
  def nearStation(userRealTime:UserRealTime, km: Double): Option[Station] = {
    //TODO 可能是个bug，可能存在序列化的问题
    val jc = RedisClusterConf.jc
    val listRes = jc.georadius(userRealTime.user.cityCode, userRealTime.location.longitude, userRealTime.location.latitude, km, GeoUnit.KM, GeoRadiusParam.geoRadiusParam.sortAscending.count(1).withCoord())
    if (listRes.isEmpty) {
      None
    }
    else {
      val coordinate = listRes.get(0).getCoordinate()
      val stationId = listRes.get(0).getMemberByString().split("-")(0)
      Some(Station(stationId, userRealTime.user.cityCode, Location(coordinate.getLongitude, coordinate.getLatitude)))
    }
  }
}

/**
  * geohash区域基本信息，district使用geohash编码表示
  * @param district
  */
case class District(district: String) extends  Validate{
  override def validate() = {
    if(district.isEmpty){
      false
    }else{
      true
    }
  }

  /**
    * 获取该geohash区域中心坐标
    * @return
    */
   def getLocation(): Location = {
    val geoHash=GeoHash.fromGeohashString(district)
    val point=geoHash.getBoundingBoxCenterPoint
    Location(point.getLongitude,point.getLatitude)
  }
}

/**
  * 用户实时坐标信息
  * @param user
  * @param location
  * @param currentTime
  */
case class UserRealTime(user: User, location: Location, currentTime: java.sql.Timestamp) extends Validate{
  override def validate() = {
    if((!user.validate())||(!location.validate())){
      false
    }else{
      true
    }
  }
}

/**
  * 用户实时区域信息，userRealTime包含了用户的实际坐标，region包含了用户所在区域的中心坐标
  * @param userRealTime
  * @param timeType
  * @param region
  */
case class UserRealTimeRegion(userRealTime:UserRealTime, timeType:String, region:Region) extends Validate{
  override def validate() = {
    if((!userRealTime.validate())||timeType.isEmpty||(!region.validate())){
      false
    }else{
      true
    }
  }
}





