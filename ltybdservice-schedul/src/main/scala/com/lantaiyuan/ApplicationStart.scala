package com.lantaiyuan

import com.lantaiyuan.utils.StartUtil

/**
  * Created by zhouyongbo on 2017-11-16.
  */
object ApplicationStart {

    def main(args: Array[String]) {
       StartUtil.startRunable(ApplicationStart.getClass)
     }
}
