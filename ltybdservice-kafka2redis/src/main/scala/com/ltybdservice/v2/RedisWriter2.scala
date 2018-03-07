package com.ltybdservice.v2

import org.apache.spark.sql.ForeachWriter
import redis.clients.jedis.Jedis


class RedisWriter2 extends ForeachWriter[BusInfo2] {
  val redisTravlKeyHead = "settle_travl_"
  val redisTravlTimeKeyHead = "settle_travltime"
  //  var jc: JedisCluster=null
  var jc: Jedis = null

  override def open(partitionId: Long, version: Long): Boolean = {
    //    jc=RedisClusterConf.jc
    jc = new Jedis("10.1.254.22", 6379)
    //TODO 没有完成有且仅有一次语义
    true
  }

  override def process(busInfo: BusInfo2): Unit = {

    val bus = busInfo.bus
    val busRealTime = busInfo.busRealTime
    //实时推送最新GPS信息
    if ((busInfo.infoType == InfoType.ITS_Topic_GIS) || (busInfo.infoType == InfoType.GPS)) {
      val key = "settle_busgps" + "_" + bus.cityCode + "_" + bus.vehicleId + "_" + busRealTime.lineId + "_" + busRealTime.direction
      val value = busRealTime.mkString()
      jc.lpush(key, value)
      if (jc.llen(key) > 30) {
        jc.ltrim(key, 0, 29)
      }
    }
    //TODO 计算趟次，将中间状态缓存于redis
  }

  override def close(errorOrNull: Throwable): Unit = {
    if (errorOrNull != null) {
      errorOrNull.printStackTrace()
      jc.close()
    }
  }
}

