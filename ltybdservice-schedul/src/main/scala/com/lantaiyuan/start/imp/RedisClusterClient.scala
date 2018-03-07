package com.lantaiyuan.start.imp

object RedisClusterClient extends Serializable{

  import redis.clients.jedis.JedisPoolConfig
  import redis.clients.jedis.JedisShardInfo
  import redis.clients.jedis.ShardedJedis
  import redis.clients.jedis.ShardedJedisPool
  import java.util

  // 构建连接池配置对象
  val poolConfig = new JedisPoolConfig
  // 设置最大连接数
  poolConfig.setMaxTotal(50)

  // 定义集群信息
  val shards = new util.ArrayList[JedisShardInfo]
  shards.add(new JedisShardInfo("10.1.254.105", 7000))
  shards.add(new JedisShardInfo("10.1.254.105", 7001))
  shards.add(new JedisShardInfo("10.1.254.105", 7002))
  shards.add(new JedisShardInfo("10.1.254.106", 7003))
  shards.add(new JedisShardInfo("10.1.254.106", 7004))
  shards.add(new JedisShardInfo("10.1.254.106", 7005))

  // 定义集群连接接
  lazy val shardedJedisPool = new ShardedJedisPool(poolConfig, shards)

  lazy val hook = new Thread {
    override def run = {
      println("Execute hook thread: " + this)
      shardedJedisPool.destroy()
    }
  }
  sys.addShutdownHook(hook.run)
}
