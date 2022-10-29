package com.megafarad.ve_scala.service.api

import com.typesafe.config.{Config, ConfigFactory}

trait Auth0Config {
  lazy val auth0enabled: Boolean = rootConfig.getBoolean("auth0.enabled")
  lazy val audience: String = rootConfig.getString("auth0.audience")
  lazy val domain: String = rootConfig.getString("auth0.domain")
  private val rootConfig: Config = ConfigFactory.load()
}
