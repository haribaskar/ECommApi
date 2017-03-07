package com.hm

import java.text.SimpleDateFormat
import java.util.Date


/**
  * Created by hari on 7/3/17.
  */


object Test {

  def main(args: Array[String]): Unit = {
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    println(df.format(new Date()))
    println(new Date().getMinutes)


  }

}
