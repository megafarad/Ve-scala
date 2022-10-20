package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.Tokenizer
import org.scalatest.flatspec.AnyFlatSpec
import scala.jdk.CollectionConverters._

class ParseTest extends AnyFlatSpec {
  private def parseIntoWords(sentence: String) = {
    val tokensList = tokenizeSentence(sentence)

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    words
  }

  behavior of "Parse"
  it should "parse お金がなければいけないです。 without exception" in {
    val sentence = "お金がなければいけないです。"
    val words = parseIntoWords(sentence)

    println(words)
  }

  it should "parse お寿司が食べたい。 without exception" in {
    val sentence = "お寿司が食べたい。"
    val words = parseIntoWords(sentence)

    println(words)
  }

  it should "parse 昨日すき焼きを食べました without exception" in {
    val sentence = "昨日すき焼きを食べました"
    val words = parseIntoWords(sentence)

    println(words)
  }

  private def tokenizeSentence(sentence: String) = {
    new Tokenizer().tokenize(sentence)
  }

  it should "parse a proper noun" in {
    val sentence = "田中さんは魚を食べました"
    val words = parseIntoWords(sentence)

    assert(words.head.partOfSpeech == Pos.ProperNoun)
  }

  it should "parse a pronoun" in {
    val sentence = "私はアメリカ人です。"
    val words = parseIntoWords(sentence)

    assert(words.head.partOfSpeech == Pos.Pronoun)
  }

  it should "parse numbers" in {
    val sentence = "三十年式歩兵銃"
    val words = parseIntoWords(sentence)
    assert(words.head.partOfSpeech == Pos.Number)
  }

  it should "parse FUKUSHIKANOU" in {
    val sentence = "午後に魚を食べた"
    val words = parseIntoWords(sentence)

    assert(words.take(2).map(_.partOfSpeech).equals(Seq(Pos.Adverb, Pos.Postposition)))
  }

  it should "parse 彼はその問題と関係がないことを明らかにした correctly" in {
    val sentence = "彼はその問題と関係がないことを明らかにした"
    val words = parseIntoWords(sentence)

    assert(words.takeRight(3).map(_.partOfSpeech).equals(Seq(Pos.Adverb, Pos.Postposition, Pos.Verb)))
  }

}
