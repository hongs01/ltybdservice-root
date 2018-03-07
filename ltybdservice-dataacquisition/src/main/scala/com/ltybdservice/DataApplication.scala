package com.ltybdservice

import com.ltybdservice.caseclass.GpsInfo
import com.ltybdservice.dao.HbaseWriter
import org.apache.spark.sql.SparkSession

object DataApplication extends App {

  val spark = SparkSession
    .builder()
//    .master("local[*]")
    .appName("DataApplication")
    .getOrCreate()

  import spark.implicits._

  val lines = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "140.143.180.132:26667,140.143.180.132:26668")
    .option("subscribe", "ITS_Topic_GIS")
    .load()
    .selectExpr("CAST(value AS STRING)")
    .as[String]

    var gps = lines
    .map(GpsInfo.getGpsInfo(_))
    .writeStream
    .foreach(new HbaseWriter)
    .start()

    gps.awaitTermination()
}
