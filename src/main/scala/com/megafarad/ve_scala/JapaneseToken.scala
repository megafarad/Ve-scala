package com.megafarad.ve_scala

case class JapaneseToken(literal: String, pos: String, pos2: String, pos3: String, pos4: String,
                         inflectionType: String, inflectionForm: String, lemma: String, reading: String,
                         hatsuon: String) extends Token
