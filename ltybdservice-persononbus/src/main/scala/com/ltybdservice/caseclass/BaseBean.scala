package com.ltybdservice.caseclass

import scala.beans.BeanProperty

trait Validate{
  def validate(): Boolean={
    true
  }
}

case class CustomCode(time :String,geocode :String) extends  Validate{
  override def validate(): Boolean = {
    if(time.isEmpty || geocode.isEmpty){
      false
    } else {
      true
    }
  }
}

case class  CodeCustomCode(time :String,geocode :String,id :String) extends  Validate{
  override def validate(): Boolean = {
    if(time.isEmpty || geocode.isEmpty || id.isEmpty){
      false
    } else {
      true
    }
  }
}
case class CodeCounts(customcode :CodeCustomCode,counts : BigInt)
case class UserCounts(customcode :CustomCode,userid :String,ucounts :BigInt)
case class BusCounts(customcode :CustomCode,busid :String,bcounts :BigInt)
case class UBCounts(customcode :CustomCode,userid :String,ucounts :BigInt,busid :String,bcounts :BigInt)
case class InfactCounts(time :Int,geocode :String,userid :String,busid :String,fcounts :BigInt)
case class PersonOnBusInfact(userid :String,busid :String,fcounts :BigInt)
case class PerOnBusMinTime(userid :String, busid :String, min_time :Int)
case class PerOnBusMaxTime(userid :String, busid :String, max_time :Int)
case class PerOnBusMinMaxTime(userid :String, busid :String, min_time :Int, max_time :Int, customcode :CustomCode, ucounts :BigInt)
case class PerOnBusCounts(userid :String, busid :String, min_time :Int, max_time :Int, ucounts :BigInt)
case class PerOnBusSumCounts(userid :String, busid :String, min_time :Int, max_time :Int, ucounts :BigInt)
case class FinaCounts(userid :String, busid :String,fcounts :BigInt, min_time :Int, max_time :Int, ucounts :BigInt)
case class FinaResults(userid :String, busid :String, min_time :String, max_time :String, fcounts :BigInt, ucounts :BigInt,probability :Double)  extends  Validate{
  override def validate(): Boolean = {
    if(ucounts < 50  || probability < 0.8 ){
      false
    } else {
      true
    }
  }
}
case class testResults(userid :String, busid :String, min_time :String, max_time :String,fcounts :BigInt, ucounts :BigInt,probability :Double)
case class testTransform(userid :String, busid :String, min_time :Long, max_time :Long,fcounts :BigInt, ucounts :BigInt,probability :Double)
case class testSort(userid :String, busid :String, min_time :Long, max_time :Long,fcounts :BigInt, ucounts :BigInt,probability :Double,result:Int)
case class BT(b:String, st: String, et: String)
case class BTH(b:String,st: String,et:String,hc:Int)
case class BTHD(b:String,st: String,et:String,hc:Int,d:String)
case class IBTH(u:String,b:String,st:String,et: String,hc:Int)
case class Struserds(userid: String, perTime: List[BTH],d:String)
case class UBInfo(id :String,geocode :String,time :String) extends Validate {
  override def validate(): Boolean = {
    if (id.isEmpty || geocode.isEmpty || time.isEmpty) {
      false
    } else {
      true
    }
  }
}

