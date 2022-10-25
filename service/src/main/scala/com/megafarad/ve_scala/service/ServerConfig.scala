package com.megafarad.ve_scala.service

import com.typesafe.config.{Config, ConfigFactory}

trait ServerConfig {
  lazy val host: String = rootConfig.getString("server.host")
  lazy val port: Int = rootConfig.getInt("server.port")
  private val rootConfig: Config = ConfigFactory.load()
}
