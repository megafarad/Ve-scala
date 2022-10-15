package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.Token

case class Word(reading: Option[String], transcription: Option[String], grammar: Grammar.Value, lemma: Option[String],
                partOfSpeech: Pos.Value, word: String, tokens: Seq[Token]) {

  def withAppendToWord(suffix: String): Word = this.copy(word = word.concat(suffix))

  def withAppendToReading(suffix: String): Word = this.copy(reading = reading match {
    case Some(value) => Some(value.concat(suffix))
    case None => Some("_".concat(suffix))
  })

  def withAppendToTranscription(suffix: String): Word = this.copy(transcription = transcription match {
    case Some(value) => Some(value.concat(suffix))
    case None => Some("_".concat(suffix))
  })

  def withAppendToLemma(suffix: String): Word = this.copy(lemma = lemma match {
    case Some(value) => Some(value.concat(suffix))
    case None => Some("_".concat(suffix))
  })



}
