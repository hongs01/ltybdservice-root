package com.ltybdservice.util

import java.text.SimpleDateFormat
import java.util.Date

object DateChangeUtil {

  def DateFormat(time:String,format:String):String={
    val sdf:SimpleDateFormat = new SimpleDateFormat(format)
    val date:String = sdf.format(new Date((time.toLong)*1000))
    date
  }

}
