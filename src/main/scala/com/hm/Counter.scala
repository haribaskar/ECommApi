package com.hm
import scala.collection.JavaConversions._
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util

/**
  * Created by hari on 6/3/17.
  */
object Counter {


  val counterMap=new util.HashMap[String,util.HashMap[Integer,Long]]()
  val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  def updateCounter(key:String,t:Long) = {
    //    println("Incrementing for "+key)
    /*if(counterMap.containsKey(key))
    {

      counterMap.put(key,(counterMap.get(key).put(1,1))}
    else
      counterMap.put(key,(0,t))*/
  counterMap.foreach(i=>{
    i._2.foreach(j=>{

      counterMap.get(key)

    })
  })

  }
  def getTimeStamp(t:Long):Long={

    val time=t-(t%(1*60*1000))
    time

  }


}
