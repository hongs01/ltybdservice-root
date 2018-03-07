package com.ltybdservice

import org.apache.spark.sql.ForeachWriter
import redis.clients.jedis.Jedis


class RedisWriter extends ForeachWriter[BusInfo]{
//  var jc: JedisCluster=null
  var  jc:Jedis=null
  override def open(partitionId: Long, version: Long): Boolean = {
//    jc=RedisClusterConf.jc
    jc = new Jedis("10.1.254.22", 6379)
    //TODO 没有完成有且仅有一次语义
    true
  }

  override def process(busInfo: BusInfo): Unit = {
    val bus=busInfo.bus
    val busRealTime=busInfo.busRealTime
    val key= "settle_busgps"+"_"+bus.cityCode+"_"+bus.vehicleId
    val value = busRealTime.mkString()
    jc.lpush(key,value)
    if(jc.llen(key) > 10){
      jc.ltrim(key,0,9)
    }
  }

  override def close(errorOrNull: Throwable): Unit = {
    if(errorOrNull!=null){
      errorOrNull.printStackTrace()
      jc.close()
    }
  }
}

