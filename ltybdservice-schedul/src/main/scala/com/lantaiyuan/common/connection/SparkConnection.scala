package com.lantaiyuan.common.connection

import com.lantaiyuan.common.config.domain.SparkConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.JavaConverters._

/**
  * Created by zhouyongbo on 2017-11-23.
  */
object SparkConnection {

  private var stream:StreamingContext = null;


  def sparkConfig(): SparkConf = {
    val conf = new SparkConf()
//            .setAppName("aaaa")
    //        .setMaster(SparkConfig.getMaster)
    //        .set("spark.executor.memory", SparkConfig.getExecutorMemory)
    //    if (SparkConfig.isExploit) {
    //      conf.setJars(SparkConfig.getJars.asScala)
    //    }
    val map = SparkConfig.getSparkConfig.asScala
    map.keys.foreach {
      key => conf.set(key, map.get(key).get)
    }
    //    conf.set("parallelExecution","false")
    //    conf.set("spark.scheduler.mode","FAIR")
    //    conf.set("spark.streaming.concurrentJobs","3")
    //    conf.set("spark.driver.allowMultipleContexts","true")
    //    val a= conf.getBoolean("spark.driver.allowMultipleContexts",true)
    //    conf.set("spark.driver.allowMultipleContexts","true")
    return conf
  }


  def streamingContext():StreamingContext ={
    this.synchronized{
      if (stream == null){
        stream = new StreamingContext(sparkConfig(),Seconds(SparkConfig.getSecond))
      }
      stream.checkpoint(SparkConfig.getCheckpoint)
      stream
    }
  }


}
