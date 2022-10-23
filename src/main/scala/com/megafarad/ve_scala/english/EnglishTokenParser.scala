package com.megafarad.ve_scala.english

import edu.stanford.nlp.simple.{Token => StanfordToken}

object EnglishTokenParser {

  def parse(stanfordToken: StanfordToken, sentenceEnding: Boolean): EnglishToken = {
    EnglishToken(literal = stanfordToken.word(),
      lemma = Option(stanfordToken.lemma()).getOrElse(""),
      pos = Option(stanfordToken.posTag()).getOrElse(""),
      sentenceEnding = sentenceEnding)
  }

  def parse(freelingOutput: String): EnglishToken = {
    val outputArray = freelingOutput.split("\\s+")
    if (outputArray.isEmpty)
      EnglishToken(literal = "",
        lemma = "",
        pos = "",
        sentenceEnding = true) else
    EnglishToken(literal = outputArray(0),
      lemma = outputArray(1),
      pos = outputArray(2),
      sentenceEnding = false)
  }
}
