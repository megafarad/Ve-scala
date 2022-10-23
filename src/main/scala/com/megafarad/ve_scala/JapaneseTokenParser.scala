package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.{Token => KuromojiToken}
import java.util

object JapaneseTokenParser {

  private val POS1 = 0
  private val POS2 = 1
  private val POS3 = 2
  private val POS4 = 3
  private val CTYPE = 4
  private val CFORM = 5
  private val BASIC = 6
  private val READING = 7
  private val PRONUNCIATION = 8

  private def getFeatureSafely(allFeaturesArray: Array[String], feature: Int): String = {
    if (feature > PRONUNCIATION) throw new IllegalStateException("Asked for a feature out of bounds.")
    if (allFeaturesArray.length >= feature + 1) allFeaturesArray(feature) else "*"
  }

  def apply(kuromojiToken: KuromojiToken): JapaneseToken = {
    val posArray = getPosArray(kuromojiToken.getAllFeaturesArray)
    JapaneseToken(literal = kuromojiToken.getSurface,
      pos = posArray(POS1),
      pos2 = posArray(POS2),
      pos3 = posArray(POS3),
      pos4 = posArray(POS4),
      inflectionType = kuromojiToken.getAllFeaturesArray()(CTYPE),
      inflectionForm = kuromojiToken.getAllFeaturesArray()(CFORM),
      lemma = kuromojiToken.getAllFeaturesArray()(BASIC),
      reading = getFeatureSafely(kuromojiToken.getAllFeaturesArray, READING),
      hatsuon = getFeatureSafely(kuromojiToken.getAllFeaturesArray, PRONUNCIATION))
  }

  def getPosArray(array: Array[String]): Array[String] = {
    util.Arrays.copyOfRange(array, POS1, POS4 + 1)
  }

  def apply(surface: String, rawFeaturesArray: String): JapaneseToken = {
    val allFeaturesArray = rawFeaturesArray.split(",")
    val posArray = getPosArray(allFeaturesArray)
    JapaneseToken(literal = surface,
      pos = posArray(POS1),
      pos2 = posArray(POS2),
      pos3 = posArray(POS3),
      pos4 = posArray(POS4),
      inflectionType = allFeaturesArray(CTYPE),
      inflectionForm = allFeaturesArray(CFORM),
      lemma = allFeaturesArray(BASIC),
      reading = getFeatureSafely(allFeaturesArray, READING),
      hatsuon = getFeatureSafely(allFeaturesArray, PRONUNCIATION))
  }

}
