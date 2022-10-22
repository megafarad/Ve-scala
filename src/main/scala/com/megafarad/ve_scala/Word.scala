package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.Token

case class Word(reading: String, transcription: String, grammar: Grammar.Value, lemma: String,
                partOfSpeech: Pos.Value, word: String, tokens: Seq[Token]) {

  def withAppendToWord(suffix: String): Word = this.copy(word = word.concat(suffix))

  def withAppendToReading(suffix: String): Word = this.copy(reading = reading.concat(suffix))

  def withAppendToTranscription(suffix: String): Word = this.copy(transcription = transcription.concat(suffix))

  def withAppendToLemma(suffix: String): Word = this.copy(lemma = lemma.concat(suffix))

  override def toString: String = this.word



}
