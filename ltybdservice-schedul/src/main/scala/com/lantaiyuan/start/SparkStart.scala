package com.lantaiyuan.start

import com.lantaiyuan.common.config.domain.{KafkaConsumeConfig, SparkConfig}
import com.lantaiyuan.common.connection.SparkConnection
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils

import scala.collection.JavaConverters._


/**
  * spark 启动类
  * Created by zhouyongbo on 2017-11-16.
  */
abstract class SparkStart extends AbstractStart{

//  def getAppName():String;
  private val second:Int = SparkConfig.getSecond;

  def kafkaConsumerConf(): Map[String, String] ={
    return Map() ++ KafkaConsumeConfig.getConfig.asScala
  }
  def kafkaConsumerConfNew(): Map[String, String] ={
    return Map() ++ KafkaConsumeConfig.getConfigNew.asScala
  }

  /**
    * kafka 连接输入流
    * @param kafkaConfig
    * @param kafkaConsums
    * @return
    */
  def kafkaConsumerInputStream(kafkaConfig:Map[String,String],kafkaConsums:Set[String],windowLength:Int):DStream[(String,String)]={
    val stramingContext = SparkConnection.streamingContext()
    var stream:InputDStream[(String,String)]= null;
    if (kafkaConfig == null ) {
      stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](stramingContext,kafkaConsumerConf(),kafkaConsums)
    }else{
      stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](stramingContext,kafkaConfig,kafkaConsums)
    }
      if(windowLength>0 && windowLength%second == 0){
        stream.window(Seconds(windowLength),Seconds(windowLength))
      }else{
        stream.window(Seconds(second),Seconds(second))
      }

  }

  /**
    * kafka 连接输入流
    * @param kafkaConfig
    * @param kafkaConsums
    * @return
    */
  def kafkaConsumerInputStream(kafkaConfig:Map[String,String],kafkaConsums:Set[String],windowLength:Int,slideInterval:Int):DStream[(String,String)]={
    val stramingContext = SparkConnection.streamingContext()
    var stream:InputDStream[(String,String)]= null;
    if (kafkaConfig == null ) {
      stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](stramingContext,kafkaConsumerConf(),kafkaConsums)
    }else{
      stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](stramingContext,kafkaConfig,kafkaConsums)
    }
    if(windowLength>0 && windowLength%slideInterval ==0 && windowLength%second == 0 && slideInterval%second == 0){
      stream.window(Seconds(windowLength),Seconds(slideInterval))
    }else{
      stream.window(Seconds(second),Seconds(second))
    }
  }

}
