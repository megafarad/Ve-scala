package com.megafarad.ve_scala.service

import org.apache.pekko.actor._
import org.apache.pekko.http.scaladsl._
import com.megafarad.ve_scala.service.api.ParseRoutes
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext

object Main extends App with ServerConfig with StrictLogging {
  implicit val system: ActorSystem = ActorSystem()

  implicit val executionContext: ExecutionContext = system.dispatcher

  val modules = new ModulesWiring with ParseRoutes {
    override def _system: ActorSystem = system
  }

  val routes = modules.parseRoutes

  logger.info(s"Server start at : $host:$port")

  Http().newServerAt(host, port).bind(routes)

}
