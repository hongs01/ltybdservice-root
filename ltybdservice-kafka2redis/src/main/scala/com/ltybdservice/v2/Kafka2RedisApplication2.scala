package com.ltybdservice.v2

import com.ltybdservice.config.KafkaConf
import org.apache.spark.sql.SparkSession

object Kafka2RedisApplication2 extends App {
  lazy val subscribeType = KafkaConf.param.getSubscribeType
  val spark = SparkSession
    .builder()
//    .master("local[*]")
    .appName("Kafka2RedisApplication2")
    .enableHiveSupport()
    .getOrCreate()

  import spark.implicits._
  /**
    * 新网关进出场数据流
    */
  val ITS_Topic_ParkIO = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "10.1.254.88:9092")
    .option(subscribeType, "ITS_Topic_ParkIO")
//    .option("startingOffsets", "earliest")
    .load()
    .selectExpr("CAST(value AS STRING)")
    .as[String]
  /**
    * 新网关进出站数据流
    */
  // {"header":{"msg_sn":0,"msg_id":257},"body":{"dev_sn":"","dev_id":3160,"city_code":"","company_code":"76","line_id":351,"in_time":1516179333,"bus_station_no":24,"station_flag":85,"station_report":1,"dis_next":0,"time_next":0,"vehicle_status":0}}
  val ITS_Topic_StationIO = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "10.1.254.88:9092")
    .option(subscribeType, "ITS_Topic_StationIO")
//    .option("startingOffsets", "earliest")
    .load()
    .selectExpr("CAST(value AS STRING)")
    .as[String]
  /**
    * 新网关实时gps数据流
    */
  //{"header":{"msg_sn":13913,"msg_id":259},"body":{"dev_sn":"L442b02cf78ff7a","dev_id":15443,"city_code":"130400","company_code":"01","line_id":781,"gps_time":1517561117,"lon":114.327000,"lat":36.686584,"angle":101,"altitude":0,"speed":0,"distance":148119,"direction":1,"dis_next":0,"time_next":0,"next_station_no":2,"vehicle_status":1}}
  val ITS_Topic_GIS = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "10.1.254.88:9092")
    .option(subscribeType, "ITS_Topic_GIS")
    .load()
    .selectExpr("CAST(value AS STRING)")
    .as[String]
  /**
    * 新网关实时gps数据流
    */
  //{"header":{"msg_sn":13913,"msg_id":259},"body":{"dev_sn":"L442b02cf78ff7a","dev_id":15443,"city_code":"130400","company_code":"01","line_id":781,"gps_time":1517561117,"lon":114.327000,"lat":36.686584,"angle":101,"altitude":0,"speed":0,"distance":148119,"direction":1,"dis_next":0,"time_next":0,"next_station_no":2,"vehicle_status":1}}
  val ITS_Topic_GIS_HD = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "10.1.254.88:9092")
    .option(subscribeType, "ITS_Topic_GIS_HD")
    .load()
    .selectExpr("CAST(value AS STRING)")
    .as[String]
  /**
    * 老网关数据流
    * 分为四种数据格式
    * 1、进出场数据流
    * 2、实时gps数据流
    * 3、进站数据流
    * 4、出站数据流
    */
  //{"azimuth":168,"battery":0,"direction":1,"eventTime":"2018-02-02 17:26:17","flag":1,"gas":0,"gprsId":61,"latitude":"36.6275565","longitude":"114.4798615","packetType":"inoutPark","packingLotNo":118,"plProperty":0,"protocolVersion":"HDBS","runstatus":131,"speed":13,"vehicleId":1066}
  //{"azimuth":302,"direction":1,"eventTime":"2018-02-02 17:22:46","gprsId":281,"gpsKm":201522,"height":0,"latitude":"36.45811166666667","longitude":"114.67322333333334","nextAway":18,"nextStationId":10,"nextTime":104,"packetType":"gps","protocolVersion":"HDBS","runstatus":3,"signal":23,"speed":15,"temp":25,"vehicleId":1077}
  //{"announcerType":0,"autoSpeakerCode":0,"currStationNo":11,"direction":1,"driverCode":4353,"gprsId":311,"inStationTime":"2018-02-02 17:22:05","outStationTime":"2018-02-02 17:22:47","packetType":"outStation","protocolVersion":"HDBS","runstatus":3,"stationIdentify":1,"vehicleId":15020}
  //{"announcerType":0,"autoSpeakerCode":0,"currStationNo":23,"direction":1,"eventTime":"2018-02-02 17:26:17","gprsId":261,"packetType":"inStation","protocolVersion":"HDBS","runstatus":3,"stationIdentify":85,"vehicleId":17177}
  val HANDAN_OLD = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "10.1.254.11:6667,10.1.254.12:6667,10.1.254.13:6667")
    .option(subscribeType, "gw2app-handan")
//    .option("startingOffsets", "earliest")
    .load()
    .selectExpr("CAST(value AS STRING)")
    .as[String]


  val unionStream = ITS_Topic_StationIO.union(ITS_Topic_GIS).union(ITS_Topic_GIS_HD).union(HANDAN_OLD)
    .map(BusInfo2.getBusInfo(_))
    .filter(_.infoType!=InfoType.OTHER)
    .writeStream
    .foreach(new RedisWriter2())
    .option("checkpointLocation", "unionStream")
    .start()
//val unionStream = ITS_Topic_GIS
//  .writeStream
//  .format("csv")        // can be "orc", "json", "csv", etc.
//  .option("path", "unionStream1.csv")
//  .option("checkpointLocation", "unionStream1")
//  .start()

  unionStream.awaitTermination()
}
