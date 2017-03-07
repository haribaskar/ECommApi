package com.hm

import java.text.SimpleDateFormat
import java.util

/**
  * Created by hari on 6/3/17.
  */
object Counter {


  val counterMap=new util.HashMap[String,(Integer,Integer)]()
  val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  def updateCounter(key:String) = {
//    println("Incrementing for "+key)
    if(counterMap.containsKey(key))
      {

      counterMap.put(key,(counterMap.get(key)._1+1,counterMap.get(key)._2))}
    else
      counterMap.put(key,(1,1))
  }
  def getTimeStamp(t:Long):Long={

    val time=t-(t%(10*60*1000))
    time

  }


}
