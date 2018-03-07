package com.ltybdservice

import com.ltybdservice.region.RegionTravelAgg
import org.apache.spark.sql.{SaveMode, SparkSession}

object PersonasApplication extends App {
  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("PersonasApplication")
    .enableHiveSupport()
    .getOrCreate()

  import spark.implicits._
  import spark.sql

  //读hive表获取原始数据信息
  val rawUserRealTime = sql(
    """SELECT trim(userId) userId,
      |trim(phoneModel) phoneModel,
      |trim(cityCode) cityCode,
      |cast(trim(longitude) as double) longitude,
      |cast(trim(latitude) as double) latitude,
      |timestamp(trim(currTime)) currentTime,
      |trim(geocode6) region
      |FROM app.dwd_user_gps_tmp
      |WHERE workdate in ('20180115','20180116','20180117','20180118','20180119') and citycode='130400'
    """.stripMargin).as[RawUserRealTime].filter(_.validate())
  //信息数据格式转换

  //偏移到附近站点
  //TODO 基于站点的计算，暂时没有用到，可能存在序列化的bug
  /*val nearDistance = 2000
  lazy val userStation = rawUserRealTime.map(raw => {
    val user = User(raw.userId, raw.phoneModel, raw.cityCode)
    val userRealTime = UserRealTime(user, Location(raw.longitude, raw.latitude), raw.currentTime)
    val station = Station.nearStation(userRealTime, nearDistance).getOrElse(Station("", "", Location(0, 0)))
    val location = station.getLocation()
    val timeType = Transform.groupType(userRealTime.currentTime, Transform.GroupTimeType.Default)
    UserRealTimeRegion(userRealTime, timeType, Region(location, station.stationId))
  }).filter(_.validate())*/
  //按geohash编码区域划分
  val userDistrict = rawUserRealTime.map(raw => {
    val user = User(raw.userId, raw.phoneModel, raw.cityCode)
    val userRealTime = UserRealTime(user, Location(raw.longitude, raw.latitude), raw.currentTime)
    val district = District(raw.region)
    val timeType = Transform.groupType(userRealTime.currentTime, Transform.GroupTimeType.Default)
    UserRealTimeRegion(userRealTime, timeType, Region(district.getLocation(), district.district))
  }).filter(_.validate())
  //求用户行程
  val travels = RegionTravelAgg.getTransferRegionTravel(spark, userDistrict)
  travels.show()

  travels.write
    .mode(SaveMode.Overwrite)
    .format("jdbc")
    .option("driver", "com.mysql.jdbc.Driver")
    .option("url", "jdbc:mysql://10.1.254.12:3306")
    .option("dbtable", "personas.personas")
    .option("user", "root")
    .option("password", "root123456")
    .save()
  spark.stop()
}
