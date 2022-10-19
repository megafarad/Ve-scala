package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.Tokenizer
import org.scalatest.flatspec.AnyFlatSpec
import scala.jdk.CollectionConverters._

class ParseTest extends AnyFlatSpec {
  behavior of "Parse"
  it should "parse お金がなければいけないです。 without exception" in {
    val sentence = "お金がなければいけないです。"
    val tokensList = tokenizeSentence(sentence)

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    println(words)
  }

  it should "parse お寿司が食べたい。 without exception" in {
    val sentence = "お寿司が食べたい。"
    val tokensList = tokenizeSentence(sentence)

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    println(words)
  }

  it should "parse 昨日すき焼きを食べました without exception" in {
    val sentence = "昨日すき焼きを食べました"
    val tokensList = tokenizeSentence(sentence)

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    println(words)
  }

  private def tokenizeSentence(sentence: String) = {
    new Tokenizer().tokenize(sentence)
  }

  it should "parse a proper noun" in {
    val sentence = "田中さんは魚を食べました"
    val tokensList = tokenizeSentence(sentence)

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    assert(words.head.partOfSpeech == Pos.ProperNoun)
  }

  it should "parse a pronoun" in {
    val sentence = "私はアメリカ人です。"
    val tokensList = tokenizeSentence(sentence)

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    assert(words.head.partOfSpeech == Pos.Pronoun)
  }

  it should "parse numbers" in {
    val sentence = "三十年式歩兵銃"
    val tokensList = tokenizeSentence(sentence)

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    println(words.flatMap(_.tokens.map(_.getAllFeatures)))
    assert(words.head.partOfSpeech == Pos.Number)
  }
}
