package com.lantaiyuan.main

import com.lantaiyuan.utils.toolUtil
import com.ltybdservice.config.KafkaConf
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

object DStream2HiveApplication extends App {

  lazy val bootstrapServers = KafkaConf.param.getBootstrapServers
  println(bootstrapServers)
  lazy val zkservers=KafkaConf.param.getZkservers
  lazy val subscribeType = KafkaConf.param.getSubscribeType
  lazy val topics = KafkaConf.param.getTopics

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> bootstrapServers,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "kafkagroup",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  val conf=new SparkConf()
            .setAppName("Kafka2HiveApplication")
            .setMaster("local[*]")
  val streamingContext=new StreamingContext(conf,Seconds(60))
  val sparkContext= streamingContext.sparkContext


  //加载kafka
  val stream_HANDAN=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-handan"), kafkaParams))
  val stream_baoji=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-baoji"), kafkaParams))
  val stream_changde=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-changde"), kafkaParams))
  val stream_chibi=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-chibi"), kafkaParams))
  val stream_kaiping=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-kaiping"), kafkaParams))
  val stream_ninghai=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-ninghai"), kafkaParams))
  val stream_shangqiu=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-shangqiu"), kafkaParams))
  val stream_tianshui=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-tianshui"), kafkaParams))
  val stream_xianning=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-xianning"), kafkaParams))
  val stream_xianyang=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-xianyang"), kafkaParams))
  val stream_xinchang=KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](Array("gw2app-xinchang"), kafkaParams))
  
  //GPS
  val stream_HANDAN_gps=stream_HANDAN.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("130400", record.value()))
  val stream_baoji_gps=stream_baoji.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("610300", record.value()))
  val stream_changde_gps=stream_changde.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("430700", record.value()))
  val stream_chibi_gps=stream_chibi.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("421281", record.value()))
  val stream_kaiping_gps=stream_kaiping.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("440783", record.value()))
  val stream_ninghai_gps=stream_ninghai.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("330226", record.value()))
  val stream_shangqiu_gps=stream_shangqiu.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("411400", record.value()))
  val stream_tianshui_gps=stream_tianshui.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("620500", record.value()))
  val stream_xianning_gps=stream_xianning.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("421200", record.value()))
  val stream_xianyang_gps=stream_xianyang.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("610400", record.value()))
  val stream_xinchang_gps=stream_xinchang.filter(x=>toolUtil.getPacketGPS(x.value())).map(record => toolUtil.getBusGps("330624", record.value()))

  //inStation
  val stream_HANDAN_IN=stream_HANDAN.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("130400", record.value()))
  val stream_baoji_IN=stream_baoji.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("610300", record.value()))
  val stream_changde_IN=stream_changde.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("430700", record.value()))
  val stream_chibi_IN=stream_chibi.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("421281", record.value()))
  val stream_kaiping_IN=stream_kaiping.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("440783", record.value()))
  val stream_ninghai_IN=stream_ninghai.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("330226", record.value()))
  val stream_shangqiu_IN=stream_shangqiu.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("411400", record.value()))
  val stream_tianshui_IN=stream_tianshui.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("620500", record.value()))
  val stream_xianning_IN=stream_xianning.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("421200", record.value()))
  val stream_xianyang_IN=stream_xianyang.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("610400", record.value()))
  val stream_xinchang_IN=stream_xinchang.filter(x=>toolUtil.getPacketIN(x.value())).map(record => toolUtil.getBusIN("330624", record.value()))

  //outStation
  val stream_HANDAN_OUT=stream_HANDAN.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("130400", record.value()))
  val stream_baoji_OUT=stream_baoji.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("610300", record.value()))
  val stream_changde_OUT=stream_changde.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("430700", record.value()))
  val stream_chibi_OUT=stream_chibi.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("421281", record.value()))
  val stream_kaiping_OUT=stream_kaiping.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("440783", record.value()))
  val stream_ninghai_OUT=stream_ninghai.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("330226", record.value()))
  val stream_shangqiu_OUT=stream_shangqiu.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("411400", record.value()))
  val stream_tianshui_OUT=stream_tianshui.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("620500", record.value()))
  val stream_xianning_OUT=stream_xianning.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("421200", record.value()))
  val stream_xianyang_OUT=stream_xianyang.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("610400", record.value()))
  val stream_xinchang_OUT=stream_xinchang.filter(x=>toolUtil.getPacketOUT(x.value())).map(record => toolUtil.getBusOUT("330624", record.value()))

  //inoutPark
  val stream_HANDAN_inoutPark=stream_HANDAN.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("130400", record.value()))
  val stream_baoji_inoutPark=stream_baoji.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("610300", record.value()))
  val stream_changde_inoutPark=stream_changde.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("430700", record.value()))
  val stream_chibi_inoutPark=stream_chibi.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("421281", record.value()))
  val stream_kaiping_inoutPark=stream_kaiping.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("440783", record.value()))
  val stream_ninghai_inoutPark=stream_ninghai.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("330226", record.value()))
  val stream_shangqiu_inoutPark=stream_shangqiu.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("411400", record.value()))
  val stream_tianshui_inoutPark=stream_tianshui.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("620500", record.value()))
  val stream_xianning_inoutPark=stream_xianning.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("421200", record.value()))
  val stream_xianyang_inoutPark=stream_xianyang.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("610400", record.value()))
  val stream_xinchang_inoutPark=stream_xinchang.filter(x=>toolUtil.getPacketinoutPark(x.value())).map(record => toolUtil.getBusinoutPark("330624", record.value()))


  val stream_gps=stream_HANDAN_gps.union(stream_baoji_gps).union(stream_changde_gps).union(stream_chibi_gps).union(stream_kaiping_gps).union(stream_ninghai_gps).union(stream_shangqiu_gps).union(stream_tianshui_gps).union(stream_xianning_gps).union(stream_xianyang_gps).union(stream_xinchang_gps)

  val stream_inStation=stream_HANDAN_IN.union(stream_baoji_IN).union(stream_changde_IN).union(stream_chibi_IN).union(stream_kaiping_IN).union(stream_ninghai_IN).union(stream_shangqiu_IN).union(stream_tianshui_IN).union(stream_xianning_IN).union(stream_xianyang_IN).union(stream_xinchang_IN)

  val stream_outStation=stream_HANDAN_OUT.union(stream_baoji_OUT).union(stream_changde_OUT).union(stream_chibi_OUT).union(stream_kaiping_OUT).union(stream_ninghai_OUT).union(stream_shangqiu_OUT).union(stream_tianshui_OUT).union(stream_xianning_OUT).union(stream_xianyang_OUT).union(stream_xinchang_OUT)

  val stream_inoutPark=stream_HANDAN_inoutPark.union(stream_baoji_inoutPark).union(stream_changde_inoutPark).union(stream_chibi_inoutPark).union(stream_kaiping_inoutPark).union(stream_ninghai_inoutPark).union(stream_shangqiu_inoutPark).union(stream_tianshui_inoutPark).union(stream_xianning_inoutPark).union(stream_xianyang_inoutPark).union(stream_xinchang_inoutPark)
  
  stream_gps.foreachRDD{ rdd=>
    val hiveContext = new HiveContext(sparkContext)
    import hiveContext.implicits._
    val gpsDataFrame = rdd.toDF()
    gpsDataFrame.registerTempTable("bus_gps")
    hiveContext.sql("set hive.exec.dynamic.partition=true")
    hiveContext.sql("set hive.exec.dynamic.partition.mode=nostrick")
    //hiveContext.sql("insert into dc.ods_gps partition(citycode,workmonth,workdate) select * from bus_gps")
    hiveContext.sql("select * from bus_gps").show()
  }

  stream_inStation.foreachRDD{ rdd=>
    val hiveContext = new HiveContext(sparkContext)
    import hiveContext.implicits._
    val inStationDataFrame = rdd.toDF()
    inStationDataFrame.registerTempTable("bus_inStation")
    hiveContext.sql("set hive.exec.dynamic.partition=true")
    hiveContext.sql("set hive.exec.dynamic.partition.mode=nostrick")
    //hiveContext.sql("insert into dc.ods_instation partition(citycode,workmonth,workdate) select * from bus_inStation")
    hiveContext.sql("select * from bus_inStation").show()
  }

  stream_outStation.foreachRDD{ rdd=>
    val hiveContext = new HiveContext(sparkContext)
    import hiveContext.implicits._
    val outStationDataFrame = rdd.toDF()
    outStationDataFrame.registerTempTable("bus_outStation")
    hiveContext.sql("set hive.exec.dynamic.partition=true")
    hiveContext.sql("set hive.exec.dynamic.partition.mode=nostrick")
    //hiveContext.sql("insert into dc.ods_outstation partition(citycode,workmonth,workdate) select * from bus_outStation")
    hiveContext.sql("select * from bus_outStation").show()
  }

  stream_inoutPark.foreachRDD{ rdd=>
    val hiveContext = new HiveContext(sparkContext)
    import hiveContext.implicits._
    val inoutParkDataFrame = rdd.toDF()
    inoutParkDataFrame.registerTempTable("bus_inoutPark")
    hiveContext.sql("set hive.exec.dynamic.partition=true")
    hiveContext.sql("set hive.exec.dynamic.partition.mode=nostrick")
    hiveContext.sql("insert into dc.ods_inoutpark partition(citycode,workmonth,workdate) select * from bus_inoutPark")
    //hiveContext.sql("select * from bus_inoutPark").show()
  }
  
  streamingContext.start()
  streamingContext.awaitTermination()
}
