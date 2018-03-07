package com.lantaiyuan.start.imp

import com.lantaiyuan.common.config.domain.{KafkaConsumeConfig, Redis}
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.JedisPool
import scala.collection.JavaConverters._

object RedisClient extends Serializable {
  var redisConfigMap=Redis.getRedisConfig.asScala
  val redisHost = /*redisConfigMap("redis.host")*/"192.168.2.72"
  val redisPort = /*redisConfigMap("redis.port")*/6379
  val redisTimeout = /*redisConfigMap("redis.timeout")*/30000
  lazy val pool = new JedisPool(new GenericObjectPoolConfig(), redisHost, redisPort.toInt, redisTimeout.toInt)

  lazy val hook = new Thread {
    override def run = {
      println("Execute hook thread: " + this)
      pool.destroy()
    }
  }
  sys.addShutdownHook(hook.run)
}
