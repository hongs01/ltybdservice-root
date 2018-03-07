package com.ltybdservice
import com.ltybdservice.caseclass._
import com.ltybdservice.utils.UntilExchange
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{ SaveMode, SparkSession}
import org.apache.spark.sql.functions.{explode}

object PersonOnBusApplication extends App {
//  val spark = SparkSession
//    .builder()
//    .master("local[*]")
//    .appName("PerBusApplication")
//    .enableHiveSupport()
//    .getOrCreate()
val spark = SparkSession
  .builder()
  .appName("PerBusApplication")
  .enableHiveSupport()
  .getOrCreate()


  import spark.sql
  import spark.implicits._

  /*按日期时间取出用户信息*/
  val calculateDate :String = "20171128"
  var userinfo= sql(s"select user_id id,geocode8 geocode,occurtime time FROM app.tmp_user_gps where workdate=$calculateDate")
    .as[UBInfo]
    .filter(_.validate())
    .map(u => UBInfo(u.id,u.geocode,UntilExchange.DateToHourMinute(u.time)))

  /*取出车辆信息*/
  var businfo = sql(s"SELECT onboardid id,geocode8 geocode,occurtime time FROM gpsdb.t_r_gpscoord_geocode where workdate=$calculateDate")
    .as[UBInfo]
    .filter(_.validate())
    .map(b=> UBInfo(b.id,b.geocode,UntilExchange.DateToHourMinute(b.time)))

  /*计算每个用户每分钟出现在某个网格的次数*/
  var usercounts = userinfo.groupByKey(u=>(CodeCustomCode(u.time,u.geocode,u.id)))
    .count()
    .map(kv => CodeCounts(kv._1,kv._2))
    .map(u => UserCounts(CustomCode(u.customcode.time,u.customcode.geocode),u.customcode.id,u.counts))

  /*计算每台车每分钟出现在某个网格的次数*/
  var buscounts = businfo.groupByKey(b => (CodeCustomCode(b.time,b.geocode,b.id)))
    .count()
    .map(kv => CodeCounts(kv._1,kv._2))
    .map(b => BusCounts(CustomCode(b.customcode.time,b.customcode.geocode),b.customcode.id,b.counts))

  /*计算人车同时出现在某一个网格的次数*/
  var incounts = usercounts.join(buscounts,"customcode")
    .as[UBCounts]
    .map(a => InfactCounts(a.customcode.time.toInt,a.customcode.geocode,a.userid,a.busid,if(a.ucounts>a.bcounts) a.bcounts else a.ucounts))

  /*计算人车重合的最小及最大时间*/
  var min_on_bus =  incounts.groupBy("userid","busid")
    .min("time")
    .withColumnRenamed("min(time)","min_time")
    .as[PerOnBusMinTime]
  var max_on_bus = incounts.groupBy("userid","busid")
    .max("time")
    .withColumnRenamed("max(time)","max_time")
    .as[PerOnBusMaxTime]

  /*计算人在最小及最大时间内出现的gps点的个数*/
  var peronbusminmax = min_on_bus.join(max_on_bus,Seq[String]("userid","busid"))
    .join(usercounts,"userid")
    .as[PerOnBusMinMaxTime]

  var peronbuscounts = peronbusminmax
    .map(p => PerOnBusCounts(p.userid,p.busid,p.min_time,p.max_time,if(p.customcode.time.toInt <= p.max_time && p.customcode.time.toInt >= p.min_time) p.ucounts else 0))

  var sumcounts = peronbuscounts.groupBy("userid","busid","min_time","max_time")
    .sum("ucounts")
    .withColumnRenamed("sum(ucounts)","ucounts")
    .as[PerOnBusSumCounts]

  /*计算人车合一概率*/
  var peronbusfact = incounts.groupBy("userid","busid")
    .sum("fcounts")
    .withColumnRenamed("sum(fcounts)","fcounts")
    .as[PersonOnBusInfact]

  var finaCounts = peronbusfact.join(sumcounts,Seq[String]("userid","busid"))
    .as[FinaCounts]

  var finaResults= finaCounts.map(f => FinaResults(f.userid,f.busid, UntilExchange.BigIntToHourMinute(f.min_time),
    UntilExchange.BigIntToHourMinute(f.max_time),f.fcounts,f.ucounts,f.fcounts.toDouble/f.ucounts.toDouble))
    finaResults.sort($"probability".desc,$"fcounts".desc).show()

    println("finaResults:")
    finaResults.show();
    println("sfinaResults:")
    val finaResults1 = finaResults.sort(finaResults("userid"), finaResults("min_time")).filter(_.validate())
    finaResults1.show();
    val userrdd: RDD[FinaResults] = finaResults1.rdd                                        //dataset转换成rdd
    val userrdd1: RDD[(String, List[BT])] = userrdd.map(a => (a.userid, BT(a.busid, a.min_time, a.max_time))).groupByKey().mapValues(v => v.toList)
    println("userrdd1:")
    userrdd1.foreach(println)
    val userrdd11 = userrdd1.map {                                                       //过滤掉被包含时间段的数据
      case (k, v) =>
        def func1(list: List[BT], head: List[BT]): List[BT] = {
          val sortList = list.sortWith(_.st < _.st)
          val newHead = sortList.head
          val nHead = newHead :: head                                                     //刚开始head为空，nHead存没有包含的元素,不断把newHead存入nHead中
          val tList = sortList.tail.filter(_.et > newHead.et)
          if (tList.isEmpty) {
            nHead.sortWith(_.st < _.st)
          }
          else {
            func1(tList, nHead)
          }
        }
        def func2(list: List[BT], head: List[BT]): List[BT] = {
          val sortList = list.sortWith(_.et > _.et)
          val newHead = sortList.head
          val nHead = newHead :: head
          val tList = sortList.tail.filter(_.st < newHead.st)
          if (tList.isEmpty) {
            nHead.sortWith(_.et > _.et)
          }
          else {
            func2(tList, nHead)
          }
        }
        (k, func2(func1(v, List[BT]()), List[BT]()).sortWith(_.st < _.st))
    }
    println("userrdd11:")
    userrdd11.foreach(println)
    val userrdd112 = userrdd11.map{
      case (k, v) =>
        var h: List[BTH] = List.empty
        if (v.length == 1) {
          h = v.map(a => BTH(a.b, a.st, a.et, 1))
          (k, h)
        }
        else {
          for (i <- 0 to v.length - 1) {
            var av: BTH = null
            i match {
              case (0) => if (v(0).et <= v(1).st) {                                          // 第一个，如果与后面不相交,为1
                av = BTH(v(i).b, v(i).st, v(i).et, 1)
                h = av :: h
              }
              else {
                av = BTH(v(i).b, v(i).st, v(i).et, 0)
                h = av :: h
              }
              case _ if i == v.length - 1 => if (v(v.length - 1).st > v(v.length - 2).et) {  // 最后一个，如果与前面不相交,为1
                av = BTH(v(i).b, v(i).st, v(i).et, 1)
                h = av :: h
              }
              else {
                av = BTH(v(i).b, v(i - 1).et, v(i).et, 3)                                     // 最后一个，如果与前面相交,为3,且将起始点改为前车的末尾点
                h = av :: h
              }
              case _ => if (v(i).st > v(i - 1).et) {                                         //如果与前面不相交
                if (v(i).et > v(i + 1).st) {                                                  //如果与前面不相交,与后面相交，为0
                  av = BTH(v(i).b, v(i).st, v(i).et, 0)
                  h = av :: h
                }
                else {
                  av = BTH(v(i).b, v(i).st, v(i).et, 1)                                       //如果与前面不相交,与后面不相交，为1
                  h = av :: h
                }
              }
              else {                                                                         //如果与前面相交
                if (v(i).et > v(i + 1).st) {                                                 //如果与前面相交,与后面相交，为2,且将起始点改为前车的末尾点
                  av = BTH(v(i).b, v(i - 1).et, v(i).et, 2)
                  h = av :: h
                }
                else {
                  av = BTH(v(i).b, v(i - 1).et, v(i).et, 3)                                  //如果与前面相交,与后面不相交，为3,且将起始点改为前车的末尾点
                  h = av :: h
                }
              }
            }
          }
          (k, h.reverse)
        }
    }
    println("userrdd112:")
    userrdd112.foreach(println)
    var userrdd1112 =userrdd112.map(f => (f._1 ,f._2,s"$calculateDate"))

    val df = userrdd1112.toDS()
    df.map(x => Struserds(x._1, x._2,x._3))
      .toDF()
      .withColumn("bth", explode($"perTime"))
      .drop($"perTime").createOrReplaceTempView("perBtime")
      val df1=sql("select userid,bth.b b,bth.st st,bth.et et,bth.hc hc,d from perBtime")
      println("df1:")
      df1.show()
    df1.write
       .mode(SaveMode.Overwrite)
       .format("jdbc")
       .option("url", "jdbc:mysql://10.1.254.12:3306")
       .option("dbtable", "personas.perBtime")
       .option("user", "root")
       .option("password", "root123456")
       .save()
       spark.stop()
}

