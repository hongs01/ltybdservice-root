package com.ltybdservice

import java.sql.Timestamp
import java.text.{DecimalFormat, NumberFormat, SimpleDateFormat}

import com.alibaba.fastjson.JSON
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object Transform {
  val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  /**
    *
    * @param double
    * @return 格式化后的经纬度
    * 格式化双精度小数点位数为6位
    */
  def fix6(double: Double): Double = {
    val df: DecimalFormat = new DecimalFormat("#.000000")
    df.format(double).toDouble
  }

  /**
    *
    * @param longitude
    * @param latitude
    * @param stationLongitude
    * @param stationLatitude
    * @return 两点的算术距离
    */
  def distance(longitude: String, latitude: String, stationLongitude: String, stationLatitude: String): Double = {
    (longitude.toDouble - stationLongitude.toDouble) * (longitude.toDouble - stationLongitude.toDouble) + (latitude.toDouble - stationLatitude.toDouble) * (latitude.toDouble - stationLatitude.toDouble)
  }

  /**
    *
    * @param timestamp
    * @return 当前时间在一天内所经过的秒数
    */
  def timestamp2DaySeconds(timestamp: Timestamp): Long = {
    import java.util.Calendar
    val cal = Calendar.getInstance
    cal.setTime(timestamp)
    cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND)
  }

  /**
    *
    * @param daySeconds
    * @return 一天内的秒数对应的时分秒字符串，xx:xx:xx
    */
  def daySeconds2HourMinutesSeconds(daySeconds:Long): String = {
    val nf = NumberFormat.getInstance
    nf.setGroupingUsed(false)
    nf.setMaximumIntegerDigits(2)
    nf.setMinimumIntegerDigits(2)
    nf.format(daySeconds / 3600) + ":" + nf.format((daySeconds % 3600) / 60) + ":" + nf.format(daySeconds % 60)
  }

  /**
    *
    * @param timestamp
    * @return 上午下午
    * E.g., at 10:04:15.250 PM the <code>AM_PM</code> is <code>PM</code>.
    */
  def ampm(timestamp: Timestamp): String = {
    import java.util.Calendar
    val cal = Calendar.getInstance
    cal.setTime(timestamp)
    cal.get(Calendar.AM_PM).toString
  }
  /**
    *
    * @param timestamp
    * @return 输入时间对应的工作日上下午，休息日上下午，周五下午,五种类型，每种类型时间内只有一趟行程
    */
  def defaultGroup(timestamp: Timestamp): String = {
    import java.util.Calendar
    val cal = Calendar.getInstance
    cal.setTime(timestamp)
    val weekDay = cal.get(Calendar.DAY_OF_WEEK)
    if(cal.get(Calendar.AM_PM) == Calendar.AM){
      if (weekDay == Calendar.SUNDAY || weekDay == Calendar.SATURDAY) {
        "weekend-am"
      }else{
        "weekday-am"
      }
    }else{
      if (weekDay == Calendar.SUNDAY || weekDay == Calendar.SATURDAY) {
        "weekend-pm"
      }else if(weekDay == Calendar.FRIDAY){
        "friday-pm"
      }else{
        "weekday-pm"
      }
    }
  }

  /**
    *
    * @param timestamp
    * @return 对应的sql date
    */
  def date(timestamp: java.sql.Timestamp): java.sql.Date = {
    import java.util.Calendar
    val cal = Calendar.getInstance
    cal.setTime(timestamp)
    new java.sql.Date(cal.getTime.getTime)
  }

  /**
    *
    * @param timestamp
    * @return 对应的sql time
    */
  def time(timestamp: java.sql.Timestamp): java.sql.Time = {
    import java.util.Calendar
    val cal = Calendar.getInstance
    cal.setTime(timestamp)
    new java.sql.Time(cal.getTime.getTime)
  }

  /**
    * 枚举 分组时间种类
    * Default 周规律，周内按工作日上下午，休息日上下午，周五下午分类
    * AMPM 按上午、下午规律统计，分上午下午
    * 周规律，周内那天分类
    */
  object GroupTimeType extends Enumeration {
    val Default = Value
    val AMPM = Value
    val WeekDay = Value
  }

  /**
    *
    * @param timestamp
    * @param groupTimeType
    * @return 分组结果的字符串表示
    */
  def groupType(timestamp: Timestamp, groupTimeType: GroupTimeType.Value): String = {
    import java.util.Calendar
    val cal = Calendar.getInstance
    cal.setTime(timestamp)
    groupTimeType match {
      case GroupTimeType.Default => defaultGroup(timestamp)
      case GroupTimeType.AMPM => cal.get(Calendar.AM_PM).toString
      case GroupTimeType.WeekDay => cal.get(Calendar.DAY_OF_WEEK).toString
    }
  }

  /**
    *
    * @param oLongitude
    * @param oLatitude
    * @param dLongitude
    * @param dLatitude
    * @param cityCode
    * @return 是否换乘，查询高德得到是否换乘
    */
  def transfer(oLongitude: Double, oLatitude: Double, dLongitude: Double, dLatitude: Double, cityCode: String): Boolean = {
    if (oLongitude < 1 || oLatitude < 1 || dLongitude < 1 || dLatitude < 1) return false
    val reqUri = new StringBuffer
    var count = 0
    reqUri.append("http://restapi.amap.com/v3/direction/transit/integrated")
      .append("?")
      .append("key=")
      .append("d5e7ef42aa54830245f9188b3e26d50d")
      .append("&")
      .append("origin=")
      .append(oLongitude.toString + "," + oLatitude.toString)
      .append("destination=")
      .append(dLongitude.toString + "," + dLatitude.toString)
      .append("&")
      .append("city=")
      .append("邯郸")
    val httpclient = HttpClients.createDefault
    try {
      val httpGet = new HttpGet(reqUri.toString)
      val response = httpclient.execute(httpGet)
      try {
        val entity = response.getEntity
        val jsonStr = EntityUtils.toString(entity)
        count = JSON.parseObject(jsonStr).getString("count").toInt
        EntityUtils.consume(entity)
      } finally if (response != null) response.close()
    } finally if (httpclient != null) httpclient.close()
    if (count > 1)
      true
    else
      false
  }
}