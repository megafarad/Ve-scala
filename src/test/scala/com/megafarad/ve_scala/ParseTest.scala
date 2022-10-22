package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.{Token, Tokenizer}
import org.mockito.Mockito
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

import scala.jdk.CollectionConverters._

class ParseTest extends AnyFlatSpec with MockitoSugar with Matchers{
  private def parseIntoWords(sentence: String) = {
    val tokensList = new Tokenizer().tokenize(sentence)

    tokensList forEach  {
      token => println(token.getSurface + " -> " + token.getAllFeatures)
    }

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    words
  }

  private def createMockToken(surface: String, rawFeaturesArray: String): Token = {
    val mockToken = mock[Token]
    val featuresArray = rawFeaturesArray.split(",")
    Mockito.when(mockToken.getAllFeaturesArray).thenReturn(featuresArray)
    Mockito.when(mockToken.getSurface).thenReturn(surface)
    Mockito.when(mockToken.getReading).thenReturn(featuresArray(7))
    mockToken

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

    words.head.partOfSpeech should be (Pos.ProperNoun)
  }

  it should "parse a pronoun" in {
    val sentence = "私はアメリカ人です。"
    val words = parseIntoWords(sentence)

    words.head.partOfSpeech should be (Pos.Pronoun)
  }

  it should "parse numbers" in {
    val sentence = "三十年式歩兵銃"
    val words = parseIntoWords(sentence)
    words.head.partOfSpeech should be (Pos.Number)
  }

  it should "parse FUKUSHIKANOU" in {
    val sentence = "午後に魚を食べた"
    val words = parseIntoWords(sentence)
    words.take(2).map(_.partOfSpeech) should be (Seq(Pos.Adverb, Pos.Postposition))
  }

  it should "parse 彼はその問題と関係がないことを明らかにした correctly" in {
    val sentence = "彼はその問題と関係がないことを明らかにした"
    val words = parseIntoWords(sentence)
    words.takeRight(3).map(_.partOfSpeech) should be (Seq(Pos.Adverb, Pos.Postposition, Pos.Verb))
  }

  it should "parse the verb in 彼女の容態は昨日悪化した。 correctly" in {
    val sentence = "彼女の容態は昨日悪化した。"
    val words = parseIntoWords(sentence)

    words(5).word should be ("悪化した")
    words(5).partOfSpeech should be (Pos.Verb)
  }

  it should "properly parse Keiyoudoushigokan" in {
    val sentence = "貴職らにとっては重要なことです。"
    val words = parseIntoWords(sentence)

    words(4).word should be ("重要な")
    words(4).partOfSpeech should be (Pos.Adjective)
  }

  it should "properly parse Naikeiyoushigokan" in {
    val sentence = "彼女は昨夜とんでもない時間に電話してきた。"
    val words = parseIntoWords(sentence)

    words(3).word should be ("とんでもない")
    words(3).partOfSpeech should be (Pos.Adjective)
  }

  it should "properly parse Meishi hijiritsu fukushikanou" in {
    val sentence = "数時間のうちにまた歯が痛くなってきた。"
    val words = parseIntoWords(sentence)

    words(1).word should be ("の")
    words(1).partOfSpeech should be (Pos.Postposition)
    words(2).word should be ("うちに")
    words(2).partOfSpeech should be (Pos.Adverb)
  }

  it should "properly parse Meishi hijiritsu jodoushigokan" in {
    val sentence = "あの人は化け物のような力持ちだ。"
    val words = parseIntoWords(sentence)

    words(4).word should be ("の")
    words(4).partOfSpeech should be (Pos.Postposition)
    words(5).word should be ("ような")
    words(5).partOfSpeech should be (Pos.Verb)

    val nextSentence = "雪のように白い。"
    val nextWords = parseIntoWords(nextSentence)

    nextWords(1).word should be ("の")
    nextWords(1).partOfSpeech should be (Pos.Postposition)
    nextWords(2).word should be ("ように")
    nextWords(2).partOfSpeech should be (Pos.Adverb)
  }

  it should "properly parse Meishi hijiritsu keiyoudoushigokan" in {
    val firstSentence = "本は友人みたいなものである。"
    val firstWords = parseIntoWords(firstSentence)

    firstWords(3).word should be ("みたいな")
    firstWords(3).partOfSpeech should be (Pos.Adjective)

    val secondWords = new Parse(Seq(
      createMockToken("みたい", "名詞,非自立,形容動詞語幹,*,*,*,みたい,ミタイ,ミタイ"),
      createMockToken("の", "助詞,連体化,*,*,*,*,の,ノ,ノ")
    )).words

    secondWords.size should be (1)
    secondWords.head.word should be ("みたいの")
    secondWords.head.partOfSpeech should be (Pos.Adjective)
    secondWords.head.reading should be(Some("ミタイノ"))

    val thirdSentence = "彼女は疲れているみたいだ。"
    val thirdWords = parseIntoWords(thirdSentence)

    thirdWords(3).word should be ("みたい")
    thirdWords(3).partOfSpeech should be (Pos.Adjective)
    thirdWords(4).word should be ("だ")
    thirdWords(4).partOfSpeech should be (Pos.Verb)
  }

  it should "properly parse Meishi tokushu jodoushigokan" in {
    val words = new Parse(Seq(
      createMockToken("行く","動詞,自立,*,*,五段・カ行促音便,基本形,行く,イク,イク"),
      createMockToken("そう","名詞,特殊,助動詞語幹,*,*,*,そう,ソウ,ソー"),
      createMockToken("だ","助動詞,*,*,*,特殊・ダ,基本形,だ,ダ,ダ")
    )).words

    words.head.word should be ("行く")
    words(1).word should be ("そう")
    words(2).word should be ("だ")
    words.forall(_.partOfSpeech.equals(Pos.Verb)) should be (true)
  }

  it should "properly parse Meishi setsubi" in {
    val sentence = "すばらしい天気は私達の楽しさを増した。"
    val words = parseIntoWords(sentence)

    words(5).word should be ("楽しさ")
    words(5).partOfSpeech should be (Pos.Noun)
  }

  it should "properly parse Meishi setsuzokushiteki" in {
    val words = new Parse(Seq(
      createMockToken("日本", "名詞,固有名詞,地域,国,*,*,日本,ニッポン,ニッポン"),
      createMockToken("対", "名詞,接続詞的,*,*,*,*,対,タイ,タイ"),
      createMockToken("アメリカ", "名詞,固有名詞,地域,国,*,*,アメリカ,アメリカ,アメリカ")
    )).words

    words.head.word should be ("日本")
    words.head.partOfSpeech should be (Pos.ProperNoun)
    words(1).word should be ("対")
    words(1).partOfSpeech should be (Pos.Conjunction)
    words(2).word should be ("アメリカ")
    words(2).partOfSpeech should be (Pos.ProperNoun)
  }

  it should "properly parse Meishi doushihijiritsuteki" in {
    val sentence = "真っ直ぐ前方を見てごらん。"
    val words = parseIntoWords(sentence)
    words(3).word should be ("見て")
    words(3).partOfSpeech should be (Pos.Verb)
    words(4).word should be ("ごらん")
    words(4).partOfSpeech should be (Pos.Verb)
  }

  it should "properly parse Settoushi" in {
    val words = new Parse(Seq(
      createMockToken("お", "接頭詞,名詞接続,*,*,*,*,お,オ,オ"),
      createMockToken("座り", "名詞,一般,*,*,*,*,座り,スワリ,スワリ")
    )).words
    words.head.word should be ("お")
    words.head.partOfSpeech should be (Pos.Prefix)
    words(1).word should be ("座り")
    words(1).partOfSpeech should be (Pos.Noun)
  }

  it should "properly parse Kigou" in {
    val sentence = "真っ直ぐ前方を見てごらん。"
    val words = parseIntoWords(sentence)

    words(5).word should be ("。")
    words(5).partOfSpeech should be (Pos.Symbol)
  }

  it should "properly parse Firaa" in {
    val words = new Parse(Seq(
      createMockToken("えと", "フィラー,*,*,*,*,*,えと,エト,エト")
    )).words

    words.head.word should be ("えと")
    words.head.partOfSpeech should be (Pos.Interjection)
  }

  it should "properly parse Sonota" in {
    val words = new Parse(Seq(
      createMockToken("だ", "助動詞,*,*,*,特殊・タ,基本形,だ,ダ,ダ"),
      createMockToken("ァ", "その他,間投,*,*,*,*,ァ,ァ,ア")
    )).words

    words.head.word should be ("だ")
    words.head.partOfSpeech should be (Pos.Postposition)
    words(1).word should be ("ァ")
    words(1).partOfSpeech should be (Pos.Other)
  }

  it should "properly parse Kandoushi" in {
    val words = new Parse(Seq(
      createMockToken("おはよう", "感動詞,*,*,*,*,*,おはよう,オハヨウ,オハヨー")
    )).words

    words.head.word should be ("おはよう")
    words.head.partOfSpeech should be (Pos.Interjection)
  }

  it should "properly parse Rentaishi" in {
    val words = new Parse(Seq(
      createMockToken("この", "連体詞,*,*,*,*,*,この,コノ,コノ")
    )).words

    words.head.word should be ("この")
    words.head.partOfSpeech should be (Pos.Determiner)
  }

  it should "properly parse Setsuzokushi" in {
    val words = new Parse(Seq(
      createMockToken("そして", "接続詞,*,*,*,*,*,そして,ソシテ,ソシテ")
    )).words

    words.head.word should be ("そして")
    words.head.partOfSpeech should be (Pos.Conjunction)
  }

  it should "properly parse Fukushi" in {
    val words = new Parse(Seq(
      createMockToken("多分", "副詞,一般,*,*,*,*,多分,タブン,タブン")
    )).words

    words.head.word should be ("多分")
    words.head.partOfSpeech should be (Pos.Adverb)
  }

  it should "properly parse Doushi" in {
    val firstWords = new Parse(Seq(
      createMockToken("行く", "動詞,自立,*,*,五段・カ行促音便,基本形,行く,イク,イク")
    )).words

    firstWords.head.word should be ("行く")
    firstWords.head.partOfSpeech should be (Pos.Verb)

    val secondWords = new Parse(Seq(
      createMockToken("行か", "動詞,自立,*,*,五段・カ行促音便,未然形,行く,イカ,イカ"),
      createMockToken("ない", "助動詞,*,*,*,特殊・ナイ,基本形,ない,ナイ,ナイ")
    )).words

    secondWords.size should be (1)
    secondWords.head.word should be ("行かない")
    secondWords.head.partOfSpeech should be (Pos.Verb)

    val thirdWords = new Parse(Seq(
      createMockToken("行っ", "動詞,自立,*,*,五段・カ行促音便,連用タ接続,行く,イッ,イッ"),
      createMockToken("て", "助詞,接続助詞,*,*,*,*,て,テ,テ"),
      createMockToken("き", "動詞,非自立,*,*,カ変・クル,連用形,くる,キ,キ"),
      createMockToken("て", "助詞,接続助詞,*,*,*,*,て,テ,テ")
    )).words

    thirdWords.size should be (1)
    thirdWords.head.word should be ("行ってきて")
    thirdWords.head.partOfSpeech should be (Pos.Verb)
    thirdWords.head.reading should contain ("イッテキテ")
  }
}
