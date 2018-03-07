package com.lantaiyuan.start.imp

import com.lantaiyuan.utils.{RedisConf, UUIDUtil}
import redis.clients.jedis.HostAndPort

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks

object ttt {
  def main(args: Array[String]): Unit = {
/*var x=Map("130400"->(2,0))
    var y=Map("130401"->(3,1))
    var sum:Integer=0
    sum+=1
    println(sum)*/
    //println(reduceMap(x,y))
//    println(mapadd())
    //ListAdd()
  //  testLoop()
    //testCompare
//    var h: List[String] = List.empty
//    val a="aaaa"
//    h= a :: h
//    System.err.print(h.size)

//    import redis.clients.jedis.JedisPoolConfig
//    import redis.clients.jedis.JedisShardInfo
//    import redis.clients.jedis.ShardedJedis
//    import redis.clients.jedis.ShardedJedisPool
//    import java.util
//    // 构建连接池配置对象// 构建连接池配置对象
//
//    val poolConfig = new JedisPoolConfig
//    // 设置最大连接数
//    poolConfig.setMaxTotal(50)
//
//    // 定义集群信息
//    val shards = new util.ArrayList[JedisShardInfo]
//    shards.add(new JedisShardInfo("10.1.254.105", 7000))
//
//    shards.add(new JedisShardInfo("10.1.254.105", 7001))
//
//    shards.add(new JedisShardInfo("10.1.254.105", 7002))
//
//    shards.add(new JedisShardInfo("10.1.254.106", 7003))
//    shards.add(new JedisShardInfo("10.1.254.106", 7004))
//    shards.add(new JedisShardInfo("10.1.254.106", 7005))
//
//
//    // 定义集群连接接
//    val shardedJedisPool = new ShardedJedisPool(poolConfig, shards)
//    var shardedJedis = shardedJedisPool.getResource
//    try { // 从连接池中获取到jedis分片对象
//      var i = 0
//      while ( {
//        i < 100
//      }) {
//        shardedJedis.set("key" + i, "value" + i)
//        {
//          i += 1; i - 1
//        }
//      }
//      // 从redis中获取数据
//      val value = shardedJedis.get("mytest")
//      System.out.println(value)
//    } catch {
//      case e: Exception =>
//        e.printStackTrace()
//    } finally if (null != shardedJedis) { // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
//      shardedJedis.close()
//    }
//
//    // 关闭连接池，正常使用的时候，不用关闭
//    shardedJedisPool.destroy()

//    import redis.clients.jedis.JedisCluster
//    val hostAndPort = new HostAndPort("",0)
//    val hostAndPortSet:Set(HostAndPort) = new Ha
//    hostAndPortSet.add(hostAndPort)
//    val jedis = new JedisCluster(hostAndPortSet)


    val jedis=RedisConf.jc
    jedis.set("lllll","12345")
    println(jedis.get("22222"))
  }



  def testCompare()={
    val userrdd1=ListBuffer((15330,"07:08","08:40"), (15332,"07:08","09:40"), (15335,"08:08","09:20"), (15334,"08:38","09:40"))
    userrdd1.last
    printf(userrdd1.size.toString)
   /* for (i<-0 to userrdd1.length-1){
      val min=userrdd1(i)._2
      val max=userrdd1(i)._3
      for (j<-i to userrdd1.length-1){
               if (userrdd1(j)._3>userrdd1(j)._3){
                 userrdd1.drop(i)
               }
      }
    }*/


    /*  val userrdd11 = userrdd1.map {
      case (k, v) =>
        var h: List[(String, String, String)] = Nil
        h=v
        for(i<-0 to h.length-1 )
        {
          val amin = h(i)._2
          val amax = h(i)._3 //第一次人车合一，取出最小时间取出最大时间
        var a: List[(String, String, String)] = Nil
          if (h.drop(i+1) != Nil) {
            a = h.drop(i+1).filter(x => x._3 > amax)
          }
          else {
            a = Nil
          }
          h = h(i) :: a


        }
        (k, h)
    }*/
  }

  def  testLoop(): Unit ={
    var a = 0;
    val numList = List(1,2,3,4,5,6,7,8,9,10);

    val loop = new Breaks;
    loop.breakable {
      for( a <- numList){
        println( "Value of a: " + a );
        if( a == 4 ){
          loop.break;
        }
      }
    }
    println( "After the loop" );
  }
  def ListAdd(): Unit ={
    var abnormalMap:ListBuffer[Map[String,Object]]=ListBuffer[Map[String,Object]]()
   // var map: Map[String, Object] = Map()
    //map += ("uuid" -> UUIDUtil.getUUIDString(""),"citycode"->"130400")
    //map += ("citycode"->"130400")
    //abnormalMap.append(map)
    var map=Map("nowtime" -> "sss","real_timePassengerFlow" -> "ddd")
    abnormalMap.append(map)
    println("aaa"+abnormalMap)
  }
  def ListAdd1(): Unit ={
    // 通过给定的函数创建 5 个元素
    val squares = List.tabulate(6)(n => n * n)
    println( "一维 : " + squares  )

    // 创建二维列表
    val mul = List.tabulate( 4,5 )( _ * _ )
    println( "多维 : " + mul  )
  }

  def reduceMap(x1:Map[String,(Int,Int)],y1:Map[String,(Int,Int)]):Map[String,(Int,Int)]= {
    var x=x1
    var y=y1
    val yk = y.last._1
    val yv = y.last._2
    println("xxxxxxxxxxxx" + yk)
    println("yyyyyyyyyyy" + yv)
    if (x.contains(yk)) {
      val xv = x.get(yk)
       x +=(yk -> (xv.get._1 + yv._1, xv.get._2 + yv._2))
      println("777" + x)
    } else {
      x=x ++ y
    }
    (x)
  }
  def mapadd()={
    var a= Map("aaa"->4)
    var b=Map("bb"->5)
   a++b
  }
}


