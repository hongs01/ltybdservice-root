package com.ltybdservice.utils

import java.text.SimpleDateFormat
import java.util.Date

object UntilExchange {

  def DateToHourMinute(time :String): String ={
    val df:SimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val begin: Date = df.parse(time)
    val hm:SimpleDateFormat=new SimpleDateFormat("HHmm")
    val hmtime:String = hm.format(begin)
    hmtime
  }

  def BigIntToHourMinute(time :BigInt) :String={
    var hmtime = (if((time / 100).toString.length ==1) "0" + (time / 100).toString else (time / 100).toString) +
      ":" +
      (if((time % 100).toString.length == 1) "0" + (time % 100).toString else (time % 100).toString)
    hmtime
  }
}
