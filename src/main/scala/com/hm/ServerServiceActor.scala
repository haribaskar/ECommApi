package com.hm

import akka.actor.Actor
import com.hm.routes.Routes

import scala.concurrent.ExecutionContext

/**
  * Created by hari on 27/2/17.
  */
class ServerServiceActor extends Actor with Routes{

  def actorRefFactory = context

  //rootRoute is defined in "com.hm.routes.Routes"
  def receive = runRoute(route)

  implicit def dispatcher: ExecutionContext = ServerActorSystem.ec
}
