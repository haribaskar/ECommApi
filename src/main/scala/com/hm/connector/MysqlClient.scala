package com.hm.connector

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Timestamp}
import java.util.Date
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import akka.actor.ActorSystem
import com.hm.Counter

import scala.collection.mutable.ArrayBuffer


/**
  * Created by hari on 27/2/17.
  */
object MysqlClient {

  private val dbc = "jdbc:mysql://" + "127.0.0.1" + ":" + 3306 + "/" + "classicmodels" + "?user=" + "root" + "&password=" + "root"
  classOf[com.mysql.jdbc.Driver]
  private var conn: Connection = null



  def getConnection: Connection = {
    if(conn ==null) {
      conn = DriverManager.getConnection(dbc)
      conn.setAutoCommit(false)
      conn
    }else if (conn.isClosed) {
      conn = DriverManager.getConnection(dbc)
      conn.setAutoCommit(false)
      conn
    }else{
      conn
    }

  }

  val autoIncValuesForTable: Map[String, Array[String]] = Map(
    "request_header" -> Array("id")

  )

  val statement=MysqlClient.getConnection.prepareStatement("insert into apiusage(userid,datetime,memory,computetime,path) values (?,?,?,?,?)")
  val updatecountstatement=MysqlClient.getConnection.prepareStatement("insert into apiusage(userid,datetime,memory,computetime,path,count,usageid) values (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE count = count + ?,userid=?")
  val selectcountstatement=MysqlClient.getConnection.prepareStatement("select count(productcode) as productcount from products where productline=?")
  def closeConnection() = conn.close()

  def executeQuery(query: String): Boolean = {
    val statement = getConnection.createStatement()
    try
      statement.execute(query)
    finally statement.close()
  }

  def getResultSet(query: String): ResultSet={
    val statement=getConnection.createStatement()
    statement.executeQuery(query)
  }

  def insert(tableName: String, elements: Map[String, Any]): Int = {
    try {
      val colNames: ArrayBuffer[String] = ArrayBuffer()
      val values: ArrayBuffer[Any] = ArrayBuffer()
      elements.foreach(i => {
        colNames += i._1
        values += i._2
      })

      val insertQuery = "INSERT INTO " + tableName + " (" + colNames.mkString(",") + ") VALUES (" + colNames.indices.map(i => "?").mkString(",") + ")"

      val returnColumns: Array[String] = autoIncValuesForTable.getOrElse(tableName, Array())
      val preparedStatement: PreparedStatement = getConnection.prepareStatement(insertQuery, returnColumns)

      values.zipWithIndex.foreach(i => addToPreparedStatement(i._1, i._2 + 1, preparedStatement))
      var generatedId: Int = 0
      try {

        preparedStatement.executeUpdate()
        if (returnColumns.nonEmpty) {
          val gkSet = preparedStatement.getGeneratedKeys
          if (gkSet.next()) {
            generatedId = gkSet.getInt(1)
          }
        }
      }
      finally preparedStatement.close()

      generatedId
    } catch {
      case e: Exception => e.printStackTrace()
        0
    }
  }
  private def addToPreparedStatement(value: Any, index: Int, preparedStatement: PreparedStatement) = {
    value match {
      case v: Long => preparedStatement.setLong(index, v)
      case v: Int => preparedStatement.setInt(index, v)
      case v: Double => preparedStatement.setDouble(index, v)
      case v: String => preparedStatement.setString(index, v)

      case v: Array[Byte] => preparedStatement.setBytes(index, v)
      case v: Serializable => preparedStatement.setObject(index, v)
      case _ => preparedStatement.setString(index, value.toString)
    }
  }


  def updateCount(userid:Int,memory:String,computetime:String,path:String,cookie:String,timestamp: Timestamp):Boolean={
    MysqlClient.updatecountstatement.setInt(1,userid)
    MysqlClient.updatecountstatement.setTimestamp(2,Timestamp.valueOf(Counter.df.format(new Date(Counter.counterMap.get(cookie)._2))))

    MysqlClient.updatecountstatement.setString(3,memory)
    MysqlClient.updatecountstatement.setString(4,computetime)
    MysqlClient.updatecountstatement.setString(5,path)
    MysqlClient.updatecountstatement.setInt(6,Counter.counterMap.get(cookie)._1)
    MysqlClient.updatecountstatement.setInt(7,1)
    print("Counter"+Counter.counterMap.get(cookie)._1)
    MysqlClient.updatecountstatement.setInt(8,Counter.counterMap.get(cookie)._1)
    MysqlClient.updatecountstatement.setInt(9,userid)
    // MysqlClient.executeQuery("insert into apiusage(userid,datetime,memory,computetime,path,count,usageid) values ("+userid+",NOW(),"+memory+","+computetime+","+path+","+1+","+1+") ON DUPLICATE KEY UPDATE count = count +"+c+"")
    true
  }


  import system.dispatcher
  import scala.concurrent.duration._
  // ...now with system in current scope:
  val system=ActorSystem("on-spray-can")
  system.scheduler.schedule(1 seconds, 5 seconds) {
    //MysqlClient.statement.executeBatch()

//    println("Batch Update "+Counter.c)


    import collection.JavaConversions._

    Counter.counterMap.toMap.foreach(i=>{
      println("User : "+ i._1+" Count : "+i._2)
      if(i._2._1!=0)
      MysqlClient.updatecountstatement.execute()

      Counter.counterMap.put(i._1,(0,0))
    })

//    if(Counter.c!=0)
//      MysqlClient.executeQuery("insert into apiusage(userid,datetime,memory,computetime,path,count,usageid) values ("+userid+",NOW(),"+memory+","+computetime+","+path+","+1+","+1+") ON DUPLICATE KEY UPDATE count = count +"+c+"")
//    Counter.c=0
    MysqlClient.getConnection.commit()
  }

}
