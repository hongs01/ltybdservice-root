package com.ltybdservice

import com.ltybdservice.config.KafkaConf
import org.apache.spark.sql.SparkSession


object Kafka2RedisApplication extends App {
  lazy val bootstrapServers = KafkaConf.param.getBootstrapServers
  lazy val subscribeType = KafkaConf.param.getSubscribeType
  lazy val topic1 = KafkaConf.param.getTopic1
  lazy val topic2 = KafkaConf.param.getTopic2
  val spark = SparkSession
    .builder()
//    .master("local[*]")
    .appName("Kafka2RedisApplication")
    .enableHiveSupport()
    .getOrCreate()

  import spark.implicits._
//TODO 没有设置检查点
  val lines = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", bootstrapServers)
    .option(subscribeType, topic1)
    //    .option("startingOffsets", "earliest")
    .load()
    .selectExpr("CAST(value AS STRING)")
    .as[String]
  val busGps = lines.filter(_.contains("vehicleId"))
    .filter(_.contains("eventTime"))
    .filter(_.contains("longitude"))
    .filter(_.contains("latitude"))
    .map(BusInfo.getBusInfo(_))
    .writeStream
    .foreach(new RedisWriter())
    //  .format("console")
    .start()

  busGps.awaitTermination()
}
