package com.lantaiyuan.common

import org.apache.spark.sql.{DataFrame, SparkSession}

object kafkaConsumerInputStream {

  def getKakfaStream(spark:SparkSession,bootstrapServers:String,zkservers:String,subscribeType:String,topic:String): DataFrame ={
      val stream=spark
        .readStream
        .format("kafka")
        .option("kafka.bootstrap.servers", bootstrapServers)
        .option("zookeeper.connect", zkservers)
        .option(subscribeType, topic)
        .load()

    return stream
  }

}
