package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.{Token, Tokenizer}
import org.scalatest.flatspec.AnyFlatSpec
import scala.jdk.CollectionConverters._

class ParseTest extends AnyFlatSpec {
  behavior of "Parse"
  it should "parse お金がなければいけないです。 without exception" in {
    val sentence = "お金がなければいけないです。"
    val tokensList = new Tokenizer().tokenize(sentence)

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    println(words)
  }

  it should "parse お寿司が食べたい。 without exception" in {
    val sentence = "お寿司が食べたい。"
    val tokensList = new Tokenizer().tokenize(sentence)

    val parser = new Parse(tokensList.asScala.toSeq)
    val words = parser.words
    println(words)
  }
}
