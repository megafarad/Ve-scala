package com.megafarad.ve_scala


case class JapaneseWord(reading: String, transcription: String, grammar: Grammar.Value, lemma: String,
                        partOfSpeech: Pos.Value, word: String, tokens: Seq[JapaneseToken]) extends Word {

  def withAppendToWord(suffix: String): JapaneseWord = this.copy(word = word.concat(suffix))

  def withAppendToReading(suffix: String): JapaneseWord = this.copy(reading = reading.concat(suffix))

  def withAppendToTranscription(suffix: String): JapaneseWord = this.copy(transcription = transcription.concat(suffix))

  def withAppendToLemma(suffix: String): JapaneseWord = this.copy(lemma = lemma.concat(suffix))

  override def toString: String = this.word



}
