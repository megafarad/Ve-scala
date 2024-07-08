package com.megafarad.ve_scala.service.api

import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import com.megafarad.ve_scala.service.application.ParseService
import com.megafarad.ve_scala.service.model.ParseRequest
import com.megafarad.ve_scala.service.model.ParseRequestSerialization._
import com.megafarad.ve_scala.service.model.ParseResponseSerialization._
import com.typesafe.scalalogging.StrictLogging
import com.github.pjfanning.pekkohttpcirce.FailFastCirceSupport._

import scala.util.{Failure, Success}

trait ParseRoutes extends StrictLogging with Auth0Config {
  val parseService: ParseService

  def parseRoutes: Route = pathPrefix("api", "parse") { _ =>
    post {
      decodeRequest {
        entity(as[ParseRequest]) { request =>
          if (auth0enabled) {
            authenticateOAuth2("KobuKobu", Auth0Authenticator(domain, audience)) {
              _ =>
                defaultHandling(request)
            }
          } else {
            defaultHandling(request)
          }
        }
      }
    }
  }

  private def defaultHandling(request: ParseRequest): Route = {
    onComplete(parseService.parse(request.language, request.text)) {
      case Failure(exception) =>
        logger.error("Unable to parse", exception)
        complete(StatusCodes.InternalServerError)
      case Success(value) =>
        complete(StatusCodes.OK, value)
    }
  }


}
