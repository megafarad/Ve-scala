package com.megafarad.ve_scala.english

import com.megafarad.ve_scala.Grammar
import com.megafarad.ve_scala.Pos
import com.megafarad.ve_scala.Word

case class EnglishWord(lemma: String, partOfSpeech: Pos.Value, grammar: Grammar.Value,
                       word: String, tokens: Seq[EnglishToken]) extends Word
