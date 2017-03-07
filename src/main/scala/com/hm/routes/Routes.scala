package com.hm.routes
import java.sql.Timestamp
import java.util.{Calendar, Date}

import com.hm.{Counter, connector}
import spray.json.{JsArray, JsNumber, JsString, _}
import com.hm.connector.MysqlClient
import spray.http.{AllOrigins, DateTime}
import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Origin`}
import spray.routing.HttpService
/**
  * Created by hari on 27/2/17.
  */
trait Routes extends HttpService with AuthenticationHandler{


  def route= {
    respondWithHeaders(List(
      `Access-Control-Allow-Origin`(AllOrigins),
      `Access-Control-Allow-Headers`("Content-Type", "Access-Control-Allow-Headers", "Authorization", "X-Requested-With")
    )) {
      path("login"){
          login
      }~path("signup"){
          signup
      }~path("logout"){
          logout
      }~path("productcountbycustomer") {
        optionalCookie("userName") {
          case Some(nameCookie) => {
            val userId = nameCookie.content.toInt
        entity(as[String]) {
          body => {


            val json = body.parseJson.asJsObject
            val id = json.getFields("id").head.asInstanceOf[JsString].value
            val startTime = System.currentTimeMillis();
              val timeStamp= Counter.df.format(new Date(Counter.getTimeStamp(startTime)))
            println("timestamp"+timeStamp)
            var x = Runtime.getRuntime().totalMemory()

            //          System.currentTimeMillis()
            // my function to create an instance of an object
            var count = getProductCountByCustomer(id.toInt)
            //count=getCount
            var y = Runtime.getRuntime().freeMemory()
            val memory = x - y


            val stopTime = System.currentTimeMillis();
            val elapsedTime = stopTime - startTime;
            //insertUsageData(userId,memory.toString,elapsedTime.toString,"productcountbycustomer")
            Counter.updateCounter(nameCookie.content,Counter.getTimeStamp(startTime))

            MysqlClient.updateCount(userId,memory.toString,elapsedTime.toString,"productcountbycustomer",nameCookie.content,Timestamp.valueOf(timeStamp))
            complete("Elapsed Time" + elapsedTime + "   count" + count + "  memory" + memory)
          }
        }
          }
          case None => {println("NO Cookie ")
            complete("No user logged in")}
        }
      }~path("productcountbyproductline") {
        optionalCookie("userName") {
          case Some(nameCookie) => {
            val userId = nameCookie.content.toInt
        entity(as[String]) {
          body => {
            val json = body.parseJson.asJsObject
            val productLine = json.getFields("productLine").head.asInstanceOf[JsString].value
            val startTime = System.currentTimeMillis();
            var x = Runtime.getRuntime().totalMemory()
            //          System.currentTimeMillis()
            // my function to create an instance of an object
            var count = getProductCountByProductLine(productLine)
            //count=getCount
            var y = Runtime.getRuntime().freeMemory()
            val memory = x - y


            val stopTime = System.currentTimeMillis();
            val elapsedTime = stopTime - startTime;
            insertUsageData(userId,memory.toString,elapsedTime.toString,"productcountbyproductline")
            complete("Elapsed Time" + elapsedTime + "  count" + count + "  memory" + memory)
          }
        }
          }
          case None => complete("No user logged in")
        }
      }
    }
  } ~ complete("Invalid Path Ecommerce App")
  def getProductCountByCustomer(id:Int):Int={
    var count=0
    val rs=MysqlClient.getResultSet("select count(p.productcode) as productcount from customers c,products p,orders o,orderdetails od where c.customerNumber=o.customerNumber and o.orderNumber=od.orderNumber and od.productCode=p.productCode and c.customerNumber="+id)
    while(rs.next())
      {
        count=rs.getInt("productcount")
      }
    count
  }

  def getProductCountByProductLine(productLine:String):Int={
    var count=0

    MysqlClient.selectcountstatement.setString(1,productLine)
    val rs= MysqlClient.selectcountstatement.executeQuery()
    while(rs.next())
    {
      count=rs.getInt("productcount")
    }
    count
  }

  def insertUsageData(userid:Int,memory:String,computetime:String,path:String):Boolean={


    MysqlClient.statement.setInt(1,userid)

    MysqlClient.statement.setTimestamp(2,new Timestamp(System.currentTimeMillis()))

    MysqlClient.statement.setString(3,memory)
    MysqlClient.statement.setString(4,computetime)
    MysqlClient.statement.setString(5,path)

    MysqlClient.statement.addBatch()


    true
  }


}
