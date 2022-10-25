package com.megafarad.ve_scala.service.model

import com.megafarad.ve_scala.english.{EnglishToken, EnglishWord}
import com.megafarad.ve_scala.{Grammar, Pos, Token, Word}
import com.megafarad.ve_scala.japanese.{JapaneseToken, JapaneseWord}
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.syntax._

case class ParseResponse(language: String, words: Seq[Word])

