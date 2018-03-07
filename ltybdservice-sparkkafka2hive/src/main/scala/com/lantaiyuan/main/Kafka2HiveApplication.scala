package com.lantaiyuan.main

import com.lantaiyuan.common.kafkaConsumerInputStream
import com.lantaiyuan.utils.toolUtil
import com.ltybdservice.config.KafkaConf
import org.apache.spark.sql.SparkSession

object Kafka2HiveApplication extends App {

  lazy val bootstrapServers = KafkaConf.param.getBootstrapServers
  lazy val zkservers=KafkaConf.param.getZkservers
  lazy val subscribeType = KafkaConf.param.getSubscribeType
  lazy val topics = KafkaConf.param.getTopics



  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("Kafka2HiveApplication")
    .enableHiveSupport()
    .getOrCreate()

  import spark.implicits._

  //TODO 没有设置检查点
  val stream_handan = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-handan").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"610300\",\"body\":"+row(0)+"}"))
  val stream_handan_top=kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-handan").selectExpr("topic","CAST(value AS STRING)")



  val stream_handan_gps=stream_handan_top.filter(row => (toolUtil.getPacketGPS(row(1)))).map(p=>toolUtil.getBusGps(p)).toDF()
  stream_handan_top.printSchema()
  stream_handan_gps.printSchema()
  stream_handan_gps.registerTempTable("bus_gps")
  //val test=spark.sql("select * from bus_gps").writeStream.outputMode("append").start()
  val test=stream_handan_gps.writeStream.outputMode("append").format("console").start()
  test.awaitTermination()


//  val stream_baoji = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-baoji").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"610300\",\"body\":"+row(0)+"}"))
//
//  val stream_changde = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-changde").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"430700\",\"body\":"+row(0)+"}"))
//
//  val stream_chibi = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-chibi").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"421281\",\"body\":"+row(0)+"}"))
//
//  val stream_kaiping = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-kaiping").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"440783\",\"body\":"+row(0)+"}"))
//
//  val stream_ninghai = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-ninghai").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"330226\",\"body\":"+row(0)+"}"))
//
//  val stream_shangqiu = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-shangqiu").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"411400\",\"body\":"+row(0)+"}"))
//
//  val stream_tianshui = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-tianshui").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"620500\",\"body\":"+row(0)+"}"))
//
//  val stream_xianning = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-xianning").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"421200\",\"body\":"+row(0)+"}"))
//
//  val stream_xianyang = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-xianyang").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"610400\",\"body\":"+row(0)+"}"))
//
//  val stream_xinchang = kafkaConsumerInputStream.getKakfaStream(spark,bootstrapServers,zkservers,subscribeType,"gw2app-xinchang").selectExpr("CAST(value AS STRING)").map(row => ("{\"citycode\":\"330624\",\"body\":"+row(0)+"}"))
//
//  val stream=stream_handan.union(stream_baoji).union(stream_changde).union(stream_chibi).union(stream_kaiping).union(stream_ninghai).union(stream_shangqiu).union(stream_tianshui).union(stream_xianning).union(stream_xianyang).union(stream_xinchang)
//
//  //val bus = stream.as[String].writeStream.outputMode("append").foreach(new HiveWriter()).start()
//  val bus = stream.as[String].writeStream.outputMode("append").foreach(new HiveWriter(sparkContext)).start()
//  bus.awaitTermination()
}
