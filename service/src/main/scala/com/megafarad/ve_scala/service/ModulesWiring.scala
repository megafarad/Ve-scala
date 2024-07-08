package com.megafarad.ve_scala.service

import org.apache.pekko.actor.ActorSystem
import com.megafarad.ve_scala.service.application.ParseService

trait ModulesWiring {

  def _system: ActorSystem

  lazy val parseService = new ParseService()(_system.dispatcher)

}
