package com.megafarad.ve_scala.service.application

import com.megafarad.ve_scala.english.StanfordNLPEn
import com.megafarad.ve_scala.japanese.KuromojiIpadic
import com.megafarad.ve_scala.service.model.ParseResponse

import scala.concurrent.{ExecutionContext, Future}

class ParseService(implicit val executionContext: ExecutionContext) {

  def parse(language: String, text: String): Future[ParseResponse] = Future {
    language match {
      case "en" | "eng" =>
        val parser = new StanfordNLPEn(text)
        ParseResponse(language, parser.words)
      case "ja" | "jpn" =>
        val parser = new KuromojiIpadic(text)
        ParseResponse(language, parser.words)
      case _ => throw new UnsupportedLanguageException(language + " is not a supported language")
    }
  }
}

class UnsupportedLanguageException(message: String) extends Exception(message)
