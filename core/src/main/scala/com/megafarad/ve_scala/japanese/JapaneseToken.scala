package com.megafarad.ve_scala.japanese

import com.megafarad.ve_scala.Token

case class JapaneseToken(literal: String, pos: String, pos2: String, pos3: String, pos4: String,
                         inflectionType: String, inflectionForm: String, lemma: String, reading: String,
                         hatsuon: String, sentenceEnding: Boolean) extends Token
