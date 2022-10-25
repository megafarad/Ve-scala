package com.megafarad.ve_scala.service.model

import com.megafarad.ve_scala.{Grammar, Pos, Token, Word}
import com.megafarad.ve_scala.english.{EnglishToken, EnglishWord}
import com.megafarad.ve_scala.japanese.{JapaneseToken, JapaneseWord}
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.syntax._

object ParseResponseSerialization {
  implicit val encodeToken: Encoder[Token] = Encoder.instance {
    case englishToken @ EnglishToken(_, _, _, _) => englishToken.asJson
    case japaneseToken @ JapaneseToken(_, _, _, _, _, _, _, _, _, _, _) => japaneseToken.asJson
  }

  implicit val grammarEncoder: Encoder[Grammar.Value] = Encoder.encodeEnumeration(Grammar)
  implicit val posEncoder: Encoder[Pos.Value] = Encoder.encodeEnumeration(Pos)

  implicit val encodeWord: Encoder[Word] = Encoder.instance {
    case japaneseWord @ JapaneseWord(_, _, _, _, _, _, _) => japaneseWord.asJson
    case englishWord @ EnglishWord(_, _, _, _, _) => englishWord.asJson
  }

  implicit val encodeParseResponse: Encoder[ParseResponse] = deriveEncoder[ParseResponse]
}