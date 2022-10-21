package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.Tokenizer
import org.scalatest.flatspec.AnyFlatSpec
import scala.jdk.CollectionConverters._

class ParseTest extends AnyFlatSpec {
  private def parseIntoWords(sentence: String) = {
    val tokensList = new Tokenizer().tokenize(sentence)

    tokensList forEach  {
      token => println(token.getSurface + " -> " + token.getAllFeatures)
    }

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

  it should "parse the verb in 彼女の容態は昨日悪化した。 correctly" in {
    val sentence = "彼女の容態は昨日悪化した。"
    val words = parseIntoWords(sentence)

    assert(words.exists(_.word.equals("悪化した")))
    assert(words.find(_.word.equals("悪化した")).exists(_.partOfSpeech == Pos.Verb))
  }

  it should "properly parse Keiyoudoushigokan" in {
    val sentence = "貴職らにとっては重要なことです。"
    val words = parseIntoWords(sentence)

    assert(words.exists(_.word.equals("重要な")))
    assert(words.find(_.word.equals("重要な")).exists(_.partOfSpeech.equals(Pos.Adjective)))
  }

  it should "properly parse Naikeiyoushigokan" in {
    val sentence = "彼女は昨夜とんでもない時間に電話してきた。"
    val words = parseIntoWords(sentence)

    assert(words.exists(_.word.equals("とんでもない")))
    assert(words.find(_.word.equals("とんでもない")).exists(_.partOfSpeech.equals(Pos.Adjective)))
  }

  it should "properly parse Meishi hijiritsu fukushikanou" in {
    val sentence = "数時間のうちにまた歯が痛くなってきた。"
    val words = parseIntoWords(sentence)

    assert(words(1).word.equals("の"))
    assert(words(1).partOfSpeech.equals(Pos.Postposition))
    assert(words(2).word.equals("うちに"))
    assert(words(2).partOfSpeech.equals(Pos.Adverb))
  }
}
