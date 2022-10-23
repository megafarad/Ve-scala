package com.megafarad.ve_scala.english

import com.megafarad.ve_scala.Token

case class EnglishToken(literal: String, lemma: String, pos: String, sentenceEnding: Boolean) extends Token
