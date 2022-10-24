package com.megafarad.ve_scala

object Pos extends Enumeration {
  type Pos = Value
  val Noun,
      ProperNoun,
      Pronoun,
      Adjective,
      Adverb,
      Determiner,
      Preposition,
      Postposition,
      Verb,
      Suffix,
      Prefix,
      Conjunction,
      Interjection,
      Number,
      Unknown,
      Symbol,
      Punctuation,
      Other,
      TBD = Value

}
