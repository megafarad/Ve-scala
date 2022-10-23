package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.Tokenizer

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class KuromojiIpadicTest extends AnyFlatSpec with Matchers {
  private def parseIntoWords(sentence: String): Seq[JapaneseWord] = {
    val tokensList = new Tokenizer().tokenize(sentence)

    tokensList forEach  {
      token => println(token.getSurface + " -> " + token.getAllFeatures)
    }

    val parser = new KuromojiIpadic(sentence)
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

    val secondWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("みたい", "名詞,非自立,形容動詞語幹,*,*,*,みたい,ミタイ,ミタイ"),
      JapaneseTokenParser.parse("の", "助詞,連体化,*,*,*,*,の,ノ,ノ")
    )).words

    secondWords.size should be (1)
    secondWords.head.word should be ("みたいの")
    secondWords.head.partOfSpeech should be (Pos.Adjective)
    secondWords.head.reading should be("ミタイノ")

    val thirdSentence = "彼女は疲れているみたいだ。"
    val thirdWords = parseIntoWords(thirdSentence)

    thirdWords(3).word should be ("みたい")
    thirdWords(3).partOfSpeech should be (Pos.Adjective)
    thirdWords(4).word should be ("だ")
    thirdWords(4).partOfSpeech should be (Pos.Verb)
  }

  it should "properly parse Meishi tokushu jodoushigokan" in {
    val words = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("行く","動詞,自立,*,*,五段・カ行促音便,基本形,行く,イク,イク"),
      JapaneseTokenParser.parse("そう","名詞,特殊,助動詞語幹,*,*,*,そう,ソウ,ソー"),
      JapaneseTokenParser.parse("だ","助動詞,*,*,*,特殊・ダ,基本形,だ,ダ,ダ")
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
    val words = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("日本", "名詞,固有名詞,地域,国,*,*,日本,ニッポン,ニッポン"),
      JapaneseTokenParser.parse("対", "名詞,接続詞的,*,*,*,*,対,タイ,タイ"),
      JapaneseTokenParser.parse("アメリカ", "名詞,固有名詞,地域,国,*,*,アメリカ,アメリカ,アメリカ")
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
    val words = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("お", "接頭詞,名詞接続,*,*,*,*,お,オ,オ"),
      JapaneseTokenParser.parse("座り", "名詞,一般,*,*,*,*,座り,スワリ,スワリ")
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
    val words = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("えと", "フィラー,*,*,*,*,*,えと,エト,エト")
    )).words

    words.head.word should be ("えと")
    words.head.partOfSpeech should be (Pos.Interjection)
  }

  it should "properly parse Sonota" in {
    val words = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("だ", "助動詞,*,*,*,特殊・タ,基本形,だ,ダ,ダ"),
      JapaneseTokenParser.parse("ァ", "その他,間投,*,*,*,*,ァ,ァ,ア")
    )).words

    words.head.word should be ("だ")
    words.head.partOfSpeech should be (Pos.Postposition)
    words(1).word should be ("ァ")
    words(1).partOfSpeech should be (Pos.Other)
  }

  it should "properly parse Kandoushi" in {
    val words = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("おはよう", "感動詞,*,*,*,*,*,おはよう,オハヨウ,オハヨー")
    )).words

    words.head.word should be ("おはよう")
    words.head.partOfSpeech should be (Pos.Interjection)
  }

  it should "properly parse Rentaishi" in {
    val words = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("この", "連体詞,*,*,*,*,*,この,コノ,コノ")
    )).words

    words.head.word should be ("この")
    words.head.partOfSpeech should be (Pos.Determiner)
  }

  it should "properly parse Setsuzokushi" in {
    val words = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("そして", "接続詞,*,*,*,*,*,そして,ソシテ,ソシテ")
    )).words

    words.head.word should be ("そして")
    words.head.partOfSpeech should be (Pos.Conjunction)
  }

  it should "properly parse Fukushi" in {
    val words = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("多分", "副詞,一般,*,*,*,*,多分,タブン,タブン")
    )).words

    words.head.word should be ("多分")
    words.head.partOfSpeech should be (Pos.Adverb)
  }

  it should "properly parse Doushi" in {
    val firstWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("行く", "動詞,自立,*,*,五段・カ行促音便,基本形,行く,イク,イク")
    )).words

    firstWords.head.word should be ("行く")
    firstWords.head.partOfSpeech should be (Pos.Verb)

    val secondWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("行か", "動詞,自立,*,*,五段・カ行促音便,未然形,行く,イカ,イカ"),
      JapaneseTokenParser.parse("ない", "助動詞,*,*,*,特殊・ナイ,基本形,ない,ナイ,ナイ")
    )).words

    secondWords.size should be (1)
    secondWords.head.word should be ("行かない")
    secondWords.head.partOfSpeech should be (Pos.Verb)

    val thirdWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("行っ", "動詞,自立,*,*,五段・カ行促音便,連用タ接続,行く,イッ,イッ"),
      JapaneseTokenParser.parse("て", "助詞,接続助詞,*,*,*,*,て,テ,テ"),
      JapaneseTokenParser.parse("き", "動詞,非自立,*,*,カ変・クル,連用形,くる,キ,キ"),
      JapaneseTokenParser.parse("て", "助詞,接続助詞,*,*,*,*,て,テ,テ")
    )).words

    thirdWords.size should be (1)
    thirdWords.head.word should be ("行ってきて")
    thirdWords.head.partOfSpeech should be (Pos.Verb)
    thirdWords.head.reading should be ("イッテキテ")
  }

  it should "properly parse Doushi setsubi" in {
    val firstWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("行か", "動詞,自立,*,*,五段・カ行促音便,未然形,行く,イカ,イカ"),
      JapaneseTokenParser.parse("れる", "動詞,接尾,*,*,一段,基本形,れる,レル,レル")
    )).words

    firstWords.size should be (1)
    firstWords.head.word should be ("行かれる")
    firstWords.head.partOfSpeech should be (Pos.Verb)

    val secondWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("食べ", "動詞,自立,*,*,一段,未然形,食べる,タベ,タベ"),
      JapaneseTokenParser.parse("させ", "動詞,接尾,*,*,一段,未然形,させる,サセ,サセ"),
      JapaneseTokenParser.parse("られ", "動詞,接尾,*,*,一段,連用形,られる,ラレ,ラレ"),
      JapaneseTokenParser.parse("た", "助動詞,*,*,*,特殊・タ,基本形,た,タ,タ")
    )).words

    secondWords.size should be(1)
    secondWords.head.lemma should be ("食べる")
    secondWords.head.word should be ("食べさせられた")
    secondWords.head.partOfSpeech should be (Pos.Verb)
  }

  it should "properly parse Doushi + jodoushi" in {
    val firstWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("食べ", "動詞,自立,*,*,一段,連用形,食べる,タベ,タベ"),
      JapaneseTokenParser.parse("まし", "助動詞,*,*,*,特殊・マス,連用形,ます,マシ,マシ"),
      JapaneseTokenParser.parse("た", "助動詞,*,*,*,特殊・タ,基本形,た,タ,タ")
    )).words

    firstWords.size should be (1)
    firstWords.head.word should be ("食べました")
    firstWords.head.partOfSpeech should be (Pos.Verb)

    val secondWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("食べ", "動詞,自立,*,*,一段,連用形,食べる,タベ,タベ,たべ/食/食べ,"),
      JapaneseTokenParser.parse("ませ", "助動詞,*,*,*,特殊・マス,未然形,ます,マセ,マセ,,"),
      JapaneseTokenParser.parse("ん", "助動詞,*,*,*,不変化型,基本形,ん,ン,ン,,")
    )).words

    secondWords.size should be (1)
    secondWords.head.word should be ("食べません")
    secondWords.head.partOfSpeech should be (Pos.Verb)

    val thirdWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("食べ", "動詞,自立,*,*,一段,連用形,食べる,タベ,タベ,たべ/食/食べ,"),
      JapaneseTokenParser.parse("て", "助詞,接続助詞,*,*,*,*,て,テ,テ,,"),
      JapaneseTokenParser.parse("いる", "動詞,非自立,*,*,一段,基本形,いる,イル,イル,,")
    )).words

    thirdWords.size should be (1)
    thirdWords.head.word should be ("食べている")
    thirdWords.head.partOfSpeech should be (Pos.Verb)

    val fourthWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("食べ", "動詞,自立,*,*,一段,連用形,食べる,タベ,タベ,たべ/食/食べ,"),
      JapaneseTokenParser.parse("てる", "動詞,非自立,*,*,一段,基本形,てる,テル,テル,,")
    )).words

    fourthWords.size should be (1)
    fourthWords.head.word should be ("食べてる")
    fourthWords.head.partOfSpeech should be (Pos.Verb)

    val fifthWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("食べ", "動詞,自立,*,*,一段,未然形,食べる,タベ,タベ,たべ/食/食べ,"),
      JapaneseTokenParser.parse("ず", "助動詞,*,*,*,特殊・ヌ,連用ニ接続,ぬ,ズ,ズ,,")
    )).words

    fifthWords.size should be (1)
    fifthWords.head.word should be ("食べず")
    fifthWords.head.partOfSpeech should be (Pos.Verb)
  }

  it should "properly parse Keiyoushi" in {
    val firstWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("寒い", "形容詞,自立,*,*,形容詞・アウオ段,基本形,寒い,サムイ,サムイ")
    )).words

    firstWords.head.word should be ("寒い")
    firstWords.head.partOfSpeech should be (Pos.Adjective)

    val secondWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("寒く", "形容詞,自立,*,*,形容詞・アウオ段,連用テ接続,寒い,サムク,サムク"),
      JapaneseTokenParser.parse("て", "助詞,接続助詞,*,*,*,*,て,テ,テ")
    )).words

    secondWords.size should be (1)
    secondWords.head.word should be ("寒くて")
    secondWords.head.partOfSpeech should be (Pos.Adjective)

    val thirdWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("寒かっ", "形容詞,自立,*,*,形容詞・アウオ段,連用タ接続,寒い,サムカッ,サムカッ"),
      JapaneseTokenParser.parse("た", "助動詞,*,*,*,特殊・タ,基本形,た,タ,タ")
    )).words

    thirdWords.size should be (1)
    thirdWords.head.word should be ("寒かった")
    thirdWords.head.partOfSpeech should be (Pos.Adjective)

    val fourthWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("寒けれ", "形容詞,自立,*,*,形容詞・アウオ段,仮定形,寒い,サムケレ,サムケレ"),
      JapaneseTokenParser.parse("ば", "助詞,接続助詞,*,*,*,*,ば,バ,バ")
    )).words

    fourthWords.size should be (1)
    fourthWords.head.word should be ("寒ければ")
    fourthWords.head.partOfSpeech should be (Pos.Adjective)

    val fifthWords = new KuromojiIpadic(Seq(
      JapaneseTokenParser.parse("寒けりゃ", "形容詞,自立,*,*,形容詞・アウオ段,仮定縮約１,寒い,サムケリャ,サムケリャ")
    )).words

    fifthWords.head.word should be ("寒けりゃ")
    fifthWords.head.partOfSpeech should be (Pos.Adjective)
  }
}
