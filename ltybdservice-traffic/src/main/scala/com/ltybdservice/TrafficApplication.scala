package com.ltybdservice

import org.apache.spark.sql.{SaveMode, SparkSession}

object TrafficApplication extends App {
  val spark = SparkSession
    .builder()
//    .master("local[*]")
    .appName("TrafficApplication")
    .getOrCreate()
  case class TransferRegionTimeTravel(userId: String,phoneModel: String, cityCode: String, timeType:String, originRegion: String, startDayTime: String, destRegion: String, endDayTime: String, transfer: Boolean)
  case class TravelsGroup(cityCode: String,originRegion: String, destRegion: String,timeType:String, startHour: String )
  case class TravelsCount(cityCode: String,originRegion: String, destRegion: String,timeType:String, startHour: String, count: Long)

  import spark.implicits._

  val userTravels = spark.read
    .format("jdbc")
    .option("driver", "com.mysql.jdbc.Driver")
    .option("url", "jdbc:mysql://192.168.2.141:3306")
    .option("dbtable", "bdapplication.personas")
    .option("user", "lty")
    .option("password", "DB*&%Khds983LVF")
    .load().as[TransferRegionTimeTravel]
  //计算每个出发目的地的乘客需求量
  val traffic = userTravels.filter(_.startDayTime != null)
    .groupByKey(ut => TravelsGroup(ut.cityCode,ut.originRegion,ut.destRegion,ut.timeType,ut.startDayTime.substring(0,2) ))
    .count()
    .map(kv => {
      val tg=kv._1
      TravelsCount(tg.cityCode,tg.originRegion,tg.destRegion,tg.timeType,tg.startHour,kv._2)
    })
    .sort($"count".desc)
//  traffic.show()
  traffic.write
    .mode(SaveMode.Overwrite)
    .format("jdbc")
    .option("driver", "com.mysql.jdbc.Driver")
    .option("url", "jdbc:mysql://192.168.2.141:3306")
    .option("dbtable", "bdapplication.traffic")
    .option("user", "lty")
    .option("password", "DB*&%Khds983LVF")
    .save()
  spark.stop()

}
