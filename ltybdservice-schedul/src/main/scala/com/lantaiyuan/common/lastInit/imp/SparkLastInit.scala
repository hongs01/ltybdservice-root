package com.lantaiyuan.common.lastInit.imp

import com.lantaiyuan.common.connection.SparkConnection
import com.lantaiyuan.common.lastInit.LastInit

/**
  * Created by zhouyongbo on 2017-11-23.
  */
class
SparkLastInit extends LastInit{

  def lastExecuteInit(): Unit = {
    print("spark启动完成")
    SparkConnection.streamingContext().start();
    SparkConnection.streamingContext().awaitTermination();

  }
}
