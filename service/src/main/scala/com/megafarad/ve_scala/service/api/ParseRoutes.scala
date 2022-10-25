package com.megafarad.ve_scala.service.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.megafarad.ve_scala.service.application.ParseService
import com.megafarad.ve_scala.service.model.ParseRequest
import com.megafarad.ve_scala.service.model.ParseRequestSerialization._
import com.megafarad.ve_scala.service.model.ParseResponseSerialization._
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.util.{Failure, Success}

trait ParseRoutes extends StrictLogging {
  val parseService: ParseService

  def parseRoutes: Route = pathPrefix("api", "parse") { something =>
    post {
      decodeRequest {
        entity(as[ParseRequest]) { request =>
          onComplete(parseService.parse(request.language, request.text)) {
            case Failure(exception) =>
              logger.error("Unable to parse", exception)
              complete(StatusCodes.InternalServerError)
            case Success(value) =>
              logger.info(something)
              complete(StatusCodes.OK, value)
          }
        }
      }
    }

  }
}
