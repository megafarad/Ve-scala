package com.megafarad.ve_scala.service.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object ParseRequestSerialization {
  implicit val parseRequestEncoder: Encoder[ParseRequest] = deriveEncoder[ParseRequest]
  implicit val parseRequestDecoder: Decoder[ParseRequest] = deriveDecoder[ParseRequest]
}