package com.megafarad.ve_scala

trait Word {
  val lemma: String
  val partOfSpeech: Pos.Value
  val word: String
  val tokens: Seq[Token]
}
