package com.megafarad.ve_scala.japanese

import com.atilika.kuromoji.ipadic.Tokenizer
import com.megafarad.ve_scala.{Grammar, Parse, Pos}

import scala.annotation.unused
import scala.jdk.CollectionConverters._

class KuromojiIpadic(tokens: Seq[JapaneseToken]) extends Parse {

  def this(text: String) = {
    this(new Tokenizer().tokenize(text).asScala.toSeq.map(JapaneseTokenParser.parse))
  }

  if (tokens.isEmpty) throw new IllegalArgumentException("Cannot parse an empty array of tokens")

  private val NO_DATA = "*"


  /**
   * Used to return the parsed [[JapaneseWord]]s.
 *
   * @return parsed [[JapaneseWord]]s.
   */
  def words: Seq[JapaneseWord] = {
    val (words, _, _)  = tokens.zipWithIndex.foldLeft[(Seq[JapaneseWord], Option[JapaneseToken], Option[TokenParseActions])]((Nil, None, None)) {
      case ((parsedWords: Seq[JapaneseWord], lastToken: Option[JapaneseToken], lastActions: Option[TokenParseActions]), (currentToken: JapaneseToken, index: Int) ) =>
        val finalSlot = parsedWords.size - 1
        if (currentToken.pos.equals(NO_DATA))
          throw new IllegalStateException("No Pos data found for token")

        val actions: Option[TokenParseActions] = if (lastActions.exists(_.eatNext)) None else currentToken.pos match {
          case MEISHI =>
            if (currentToken.pos2.equals(NO_DATA)) {
              Some(TokenParseActions(pos = Pos.Noun))
            } else {
              currentToken.pos2 match {
                case KOYUUMEISHI =>
                  Some(TokenParseActions(pos = Pos.ProperNoun))
                case DAIMEISHI =>
                  Some(TokenParseActions(pos = Pos.Pronoun))
                case FUKUSHIKANOU | SAHENSETSUZOKU | KEIYOUDOUSHIGOKAN | NAIKEIYOUSHIGOKAN =>
                  if (index == tokens.length - 1) {
                    Some(TokenParseActions(pos = Pos.Noun))
                  } else {
                    val following = tokens(index + 1)
                    following.inflectionType match {
                      case SAHEN_SURU => Some(TokenParseActions(pos = Pos.Verb, eatNext = true))
                      case TOKUSHU_DA =>
                        if (following.inflectionForm.equals(TAIGENSETSUZOKU)) {
                          Some(TokenParseActions(pos = Pos.Adjective, eatNext = true, eatLemma = false))
                        } else {
                          Some(TokenParseActions(pos = Pos.Adjective))
                        }
                      case TOKUSHU_NAI => Some(TokenParseActions(pos = Pos.Adjective, eatNext = true))
                      case _ => if (following.pos.equals(JOSHI))
                        Some(TokenParseActions(pos = Pos.Adverb)) else Some(TokenParseActions(pos = Pos.Noun))

                    }
                  }
                case HIJIRITSU | TOKUSHU =>
                  if (currentToken.pos3.equals(NO_DATA) || index == tokens.length - 1)
                    Some(TokenParseActions(pos = Pos.Noun)) else {
                    val following = tokens(index + 1)
                    currentToken.pos3 match {
                      case FUKUSHIKANOU =>
                        if (following.pos.equals(JOSHI) &&
                          following.literal.equals(NI)) Some(TokenParseActions(pos = Pos.Adverb, eatNext = true)) else
                          Some(TokenParseActions(pos = Pos.Noun))

                      case JODOUSHIGOKAN =>
                        if (following.inflectionType.equals(TOKUSHU_DA))
                          Some(TokenParseActions(pos = Pos.Verb, grammar = Grammar.Auxiliary,
                            eatNext = following.inflectionForm.equals(TAIGENSETSUZOKU))) else
                          if (following.pos.equals(JOSHI) &&
                          following.pos2.equals(FUKUSHIKA))
                            Some(TokenParseActions(pos = Pos.Adverb, eatNext = true))
                          else Some(TokenParseActions(pos = Pos.Noun))

                      case KEIYOUDOUSHIGOKAN =>
                        Some(TokenParseActions(pos = Pos.Adjective,
                          eatNext = following.inflectionType.equals(TOKUSHU_DA) &&
                            following.inflectionForm.equals(TAIGENSETSUZOKU) ||
                              following.pos2.equals(RENTAIKA)))

                      case _ => Some(TokenParseActions(pos = Pos.Noun))
                    }
                  }
                case KAZU =>
                  if (parsedWords.nonEmpty && parsedWords(finalSlot).partOfSpeech.equals(Pos.Number))
                    Some(TokenParseActions(pos = Pos.Number, attachToPrevious = true, alsoAttachToLemma = true))
                    else Some(TokenParseActions(pos = Pos.Number))

                case SETSUBI =>
                  if (currentToken.pos3.equals(JINMEI)) Some(TokenParseActions(pos = Pos.Suffix)) else
                    if (currentToken.pos3.equals(TOKUSHU) && currentToken.lemma.equals(SA))
                      Some(TokenParseActions(pos = Pos.Noun, updatePos = true, attachToPrevious = true)) else
                      Some(TokenParseActions(pos = Pos.Noun, alsoAttachToLemma = true, attachToPrevious = true))

                case SETSUZOKUSHITEKI => Some(TokenParseActions(pos = Pos.Conjunction))

                case DOUSHIHIJIRITSUTEKI => Some(TokenParseActions(pos = Pos.Verb, grammar = Grammar.Nominal))

                case _ => Some(TokenParseActions(pos = Pos.Noun))

              }
            }
          case SETTOUSHI => Some(TokenParseActions(pos = Pos.Prefix))
          case JODOUSHI =>
            val defaultPos = Pos.Postposition
            val qualifyingList = Seq(TOKUSHU_TA, TOKUSHU_NAI, TOKUSHU_TAI, TOKUSHU_MASU, TOKUSHU_NU)
            if (lastToken.isEmpty || !lastToken.get.pos2.equals(KAKARIJOSHI) &&
              qualifyingList.contains(currentToken.inflectionType))
              Some(TokenParseActions(pos = defaultPos, attachToPrevious = true))
            else if (currentToken.inflectionType.equals(FUHENKAGATA) &&
              currentToken.lemma.equals(NN))
              Some(TokenParseActions(pos = defaultPos, attachToPrevious = true))
            else if ((currentToken.inflectionType.equals(TOKUSHU_DA) ||
              currentToken.inflectionType.equals(TOKUSHU_DESU)) && !currentToken.literal.equals(NA))
              Some(TokenParseActions(pos = Pos.Verb))
            else Some(TokenParseActions(pos = defaultPos))

          case DOUSHI =>
            val pos = Pos.Verb
            currentToken.pos2 match {
              case SETSUBI => Some(TokenParseActions(pos = pos, attachToPrevious = true))
              case HIJIRITSU => if (!currentToken.inflectionForm.equals(MEIREI_I))
                Some(TokenParseActions(pos = pos, attachToPrevious = true))
                else Some(TokenParseActions(pos = pos))
              case _ => Some(TokenParseActions(pos = pos))
            }
          case KEIYOUSHI => Some(TokenParseActions(pos = Pos.Adjective))
          case JOSHI =>
            val qualifyingList = Seq(TE, DE, BA)
            if (currentToken.pos2.equals(SETSUZOKUJOSHI) && qualifyingList.contains(currentToken.literal))
              Some(TokenParseActions(pos = Pos.Postposition, attachToPrevious = true))
            else Some(TokenParseActions(pos = Pos.Postposition))
          case RENTAISHI => Some(TokenParseActions(pos = Pos.Determiner))
          case SETSUZOKUSHI => Some(TokenParseActions(pos = Pos.Conjunction))
          case FUKUSHI => Some(TokenParseActions(pos = Pos.Adverb))
          case KIGOU => Some(TokenParseActions(pos = Pos.Symbol))
          case FIRAA | KANDOUSHI => Some(TokenParseActions(pos = Pos.Interjection))
          case SONOTA => Some(TokenParseActions(pos = Pos.Other))
          case _ => Some(TokenParseActions(pos = Pos.TBD))
        }

        val updatedWords: Seq[JapaneseWord] = actions match {
          case Some(parsedActions) => if (parsedActions.attachToPrevious && parsedWords.nonEmpty) {
            parsedWords match {
              case init :+ last =>
                val baseAttachedPrevious: JapaneseWord = last.copy(tokens = last.tokens :+ currentToken)
                  .withAppendToWord(currentToken.literal)
                  .withAppendToReading(currentToken.reading)
                  .withAppendToTranscription(currentToken.hatsuon)
                val withAttachedLemma: JapaneseWord = if (parsedActions.alsoAttachToLemma)
                  baseAttachedPrevious.withAppendToLemma(currentToken.lemma) else baseAttachedPrevious
                val withUpdatePos: JapaneseWord = if (parsedActions.updatePos) withAttachedLemma.copy(partOfSpeech = parsedActions.pos) else withAttachedLemma
                init :+ withUpdatePos
              case _ => Nil
            }
          } else {
            val word = JapaneseWord(reading = currentToken.reading,
              transcription = currentToken.hatsuon,
              grammar = parsedActions.grammar,
              lemma = currentToken.lemma,
              partOfSpeech = parsedActions.pos,
              word = currentToken.literal,
              tokens = Seq(currentToken))
            val withEatNext: JapaneseWord = if (parsedActions.eatNext) {
              if (index == tokens.length - 1) throw new IllegalStateException("There's a path that allows array overshooting.")
              val following: JapaneseToken = tokens(index + 1)
              val withMost: JapaneseWord = word.copy(tokens = word.tokens :+ following)
                .withAppendToWord(following.literal)
                .withAppendToReading(following.reading)
                .withAppendToTranscription(following.hatsuon)
              if (parsedActions.eatLemma) withMost.withAppendToLemma(following.lemma) else withMost
            } else word
            parsedWords :+ withEatNext
          }
          case None => parsedWords
        }

        (updatedWords, Some(currentToken), actions)
    }
    words
  }



  private case class TokenParseActions(pos: Pos.Value = Pos.TBD, eatNext: Boolean = false, eatLemma: Boolean = true,
                                       attachToPrevious: Boolean = false, alsoAttachToLemma: Boolean = false,
                                       updatePos: Boolean = false, grammar: Grammar.Value = Grammar.Unassigned)

  // POS1
  private val MEISHI = "??????"
  private val KOYUUMEISHI = "????????????"
  private val DAIMEISHI = "?????????"
  private val JODOUSHI = "?????????"
  private val KAZU = "???"
  private val JOSHI = "??????"
  private val SETTOUSHI = "?????????"
  private val DOUSHI = "??????"
  private val KIGOU = "??????"
  private val FIRAA = "????????????"
  private val SONOTA = "?????????"
  private val KANDOUSHI = "?????????"
  private val RENTAISHI = "?????????"
  private val SETSUZOKUSHI = "?????????"
  private val FUKUSHI = "??????"
  private val SETSUZOKUJOSHI = "????????????"
  private val KEIYOUSHI = "?????????"
  @unused
  private val MICHIGO = "?????????"

  // POS2_BLACKLIST and inflection types
  private val HIJIRITSU = "?????????"
  private val FUKUSHIKANOU = "????????????"
  private val SAHENSETSUZOKU = "????????????"
  private val KEIYOUDOUSHIGOKAN = "??????????????????"
  private val NAIKEIYOUSHIGOKAN = "?????????????????????"
  private val JODOUSHIGOKAN = "???????????????"
  private val FUKUSHIKA = "?????????"
  private val TAIGENSETSUZOKU = "????????????"
  private val RENTAIKA = "?????????"
  private val TOKUSHU = "??????"
  private val SETSUBI = "??????"
  private val SETSUZOKUSHITEKI = "????????????"
  private val DOUSHIHIJIRITSUTEKI = "??????????????????"
  private val SAHEN_SURU = "???????????????"
  private val TOKUSHU_TA = "????????????"
  private val TOKUSHU_NAI = "???????????????"
  private val TOKUSHU_TAI = "???????????????"
  private val TOKUSHU_DESU = "???????????????"
  private val TOKUSHU_DA = "????????????"
  private val TOKUSHU_MASU = "???????????????"
  private val TOKUSHU_NU = "????????????"
  private val FUHENKAGATA = "????????????"
  private val JINMEI = "??????"
  private val MEIREI_I = "?????????"
  private val KAKARIJOSHI = "?????????"
  @unused
  private val KAKUJOSHI = "?????????"

  // etc
  private val NA = "???"
  private val NI = "???"
  private val TE = "???"
  private val DE = "???"
  private val BA = "???"
  private val NN = "???"
  private val SA = "???"
}
