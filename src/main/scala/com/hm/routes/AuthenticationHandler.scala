package com.hm.routes

import com.hm.connector.MysqlClient
import spray.http.HttpCookie
import spray.json.{JsString, _}
import spray.routing.HttpService

/**
  * Created by vishnu on 2/17/17.
  */
trait AuthenticationHandler extends HttpService{


  def login = post{
    entity(as[String])
    {
      body=>{
        val json=body.parseJson.asJsObject
        val u=json.getFields("userName").head.asInstanceOf[JsString].value
        val userName:String=if(json.getFields("userName").nonEmpty){

          json.getFields("userName").head.asInstanceOf[JsString].value
        }
        else
        {
          ""
        }
        val password = if(json.getFields("password").nonEmpty){
          json.getFields("password").head.asInstanceOf[JsString].value
        } else {
          ""
        }
        if(loginCheck(userName,password)._1){
          val uid=loginCheck(userName,password)._2
          setCookie(HttpCookie("userName",content = ""+uid)) {
            complete("User has logged in ")
          }
        }else {
          complete("wrong credentials")
        }
      }
    }

  }
  def signup =post{
    entity(as[String])
    {
      body=>{
        val json=body.parseJson.asJsObject
        val name=json.getFields("name").head.asInstanceOf[JsString].value
        val userName=json.getFields("userName").head.asInstanceOf[JsString].value
        val password=json.getFields("password").head.asInstanceOf[JsString].value
        if(!registerUser(name,userName,password)) {
          complete("signup successful")
        }
        else {
          complete("signup failed")
        }
      }
    }

  }
  def logout=deleteCookie("userName")
  {
    complete("user Logged out")
  }
  def loginCheck(username:String,password:String)={
    val rs = MysqlClient.getResultSet("select * from user where user_name='"+username+"' AND password='"+password+"'")
    val response = if(rs.next()){
      (true,rs.getInt("userid"))

    }else{
      println("Invalid session")
      (false,0)
    }
    rs.close()
    response
  }
  def registerUser(name:String,userName:String,password:String)={
    val rs=MysqlClient.executeQuery("insert into user(name,user_name,password) values ('"+name+"','"+userName+"','"+password+"')")
    rs
  }

}
