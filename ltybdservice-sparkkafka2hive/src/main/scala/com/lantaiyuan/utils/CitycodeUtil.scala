package com.lantaiyuan.utils

object CitycodeUtil {

  val citycodeMap=Map(
    "gw2app-handan"->"130400",
    "gw2app-baoji"->"610300",
    "gw2app-changde"->"",
    "gw2app-chibi"->"421281",
    "gw2app-kaiping"->"440783",
    "gw2app-liuzhou"->"450200",
    "gw2app-ninghai"->"330226",
    "gw2app-shangqiu"->"411400",
    "gw2app-tianshui"->"620500",
    "gw2app-xianning"->"421200",
    "gw2app-xianyang"->"610400",
    "gw2app-xinchang"->"330624"
  )

  def getCitycode(topic:String):String={
    citycodeMap(topic)
  }

}
