package com.lantaiyuan.start

/**
  * Created by zhouyongbo on 2017-11-16.
  */
abstract class AbstractStart {


   def doBefor();

  def befor(){
    doBefor();
  }

  def doExecute();

  def execute(){
    befor();
    doExecute();
    after();
  }

  def doAfter();

  def after(){
    doAfter()

  }



}
