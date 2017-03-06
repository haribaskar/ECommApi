package com.hm

import java.util

/**
  * Created by hari on 6/3/17.
  */
object Counter {


  val counterMap=new util.HashMap[String,(Integer,Integer)]()
  val time=(System.currentTimeMillis()*1000)%10
  def updateCounter(key:String) = {
//    println("Incrementing for "+key)
    if(counterMap.containsKey(key)&&time!=0)
      {
        println("Time"+time)
      counterMap.put(key,(counterMap.get(key)._1+1,counterMap.get(key)._2+time.toInt))}
    else
      counterMap.put(key,(1,time.toInt))
  }


}
