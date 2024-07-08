package com.megafarad.ve_scala.service.api

import org.apache.pekko.http.scaladsl.server.directives.{Credentials, SecurityDirectives}
import com.auth0.jwk.UrlJwkProvider
import pdi.jwt.{JwtAlgorithm, JwtBase64, JwtCirce, JwtClaim}

import java.time.Clock
import scala.util.{Failure, Success, Try}

object Auth0Authenticator {

  // A regex that defines the JWT pattern and allows us to
  // extract the header, claims and signature
  private val jwtRegex = """(.+?)\.(.+?)\.(.+?)""".r

  def apply(domain: String, audience: String): SecurityDirectives.Authenticator[JwtClaim] = {
    case Credentials.Missing => None
    case Credentials.Provided(identifier) => validateJwt(identifier, domain, audience).toOption
  }

  def validateJwt(token: String, domain: String, audience: String): Try[JwtClaim] = for {
    jwk <- getJwk(token, domain)
    claims <- JwtCirce.decode(token, jwk.getPublicKey, Seq(JwtAlgorithm.RS256))
    _ <- validateClaims(claims, s"https://$domain/", audience)
  } yield claims

  private val splitToken = (jwt: String) => jwt match {
    case jwtRegex(header, body, sig) => Success((header, body, sig))
    case _ => Failure(new Exception("Token does not match the correct pattern"))
  }

  private val decodeElements = (data: Try[(String, String, String)]) => data map {
    case (header, body, sig) => (JwtBase64.decodeString(header), JwtBase64.decodeString(body), sig)
  }

  private val getJwk = (token: String, domain: String) =>
    (splitToken andThen decodeElements) (token) flatMap {
      case (header, _, _) =>
        val jwtHeader = JwtCirce.parseHeader(header) // extract the header
        val jwkProvider = new UrlJwkProvider(s"https://$domain")

        // Use jwkProvider to load the JWKS data and return the JWK
        jwtHeader.keyId.map(k => Try(jwkProvider.get(k))).getOrElse(Failure(new Exception("Unable to retrieve kid")))
    }

  private val validateClaims = (claims: JwtClaim, issuer: String, audience: String) => {
    implicit val clock: Clock = Clock.systemDefaultZone()
    if (claims.isValid(issuer, audience)) {
      Success(claims)
    } else {
      Failure(new Exception("The JWT did not pass validation"))
    }
  }


}
