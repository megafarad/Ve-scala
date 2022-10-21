package com.megafarad.ve_scala

import com.atilika.kuromoji.ipadic.Token

import java.util
import scala.annotation.unused

/**
 * Used to parse Kuromoji tokens into "[[Word]]s."
 *
 * @param tokenSeq Tokens to parse
 */
class Parse(tokenSeq: Seq[Token]) {

  if (tokenSeq.isEmpty) throw new IllegalArgumentException("Cannot parse an empty array of tokens")

  private val NO_DATA = "*"
  private val POS1 = 0
  private val POS2 = 1
  private val POS3 = 2
  private val POS4 = 3
  private val CTYPE = 4
  private val CFORM = 5
  private val BASIC = 6
  private val READING = 7
  private val PRONUNCIATION = 8

  /**
   * Used to return the parsed [[Word]]s.
   * @return parsed [[Word]]s.
   */
  def words: Seq[Word] = {
    val (words, _, _)  = tokenSeq.zipWithIndex.foldLeft[(Seq[Word], Option[Token], Option[TokenParseActions])]((Nil, None, None)) {
      case ((parsedWords: Seq[Word], lastToken: Option[Token], lastActions: Option[TokenParseActions]), (currentToken: Token, index: Int) ) =>
        val finalSlot = parsedWords.size - 1
        val currentPOSArray = util.Arrays.copyOfRange(currentToken.getAllFeaturesArray, POS1, POS4 + 1)
        if (currentPOSArray.isEmpty || currentPOSArray(POS1).equals(NO_DATA))
          throw new IllegalStateException("No Pos data found for token")

        val actions: Option[TokenParseActions] = if (lastActions.exists(_.eatNext)) None else currentPOSArray(POS1) match {
          case MEISHI =>
            if (currentPOSArray(POS2).equals(NO_DATA)) {
              Some(TokenParseActions(pos = Pos.Noun))
            } else {
              currentPOSArray(POS2) match {
                case KOYUUMEISHI =>
                  Some(TokenParseActions(pos = Pos.ProperNoun))
                case DAIMEISHI =>
                  Some(TokenParseActions(pos = Pos.Pronoun))
                case FUKUSHIKANOU | SAHENSETSUZOKU | KEIYOUDOUSHIGOKAN | NAIKEIYOUSHIGOKAN =>
                  if (index == tokenSeq.length - 1) {
                    Some(TokenParseActions(pos = Pos.Noun))
                  } else {
                    val following = tokenSeq(index + 1)
                    val followingPOSArray = util.Arrays.copyOfRange(following.getAllFeaturesArray, POS1, POS4 + 1)
                    following.getAllFeaturesArray()(CTYPE) match {
                      case SAHEN_SURU => Some(TokenParseActions(pos = Pos.Verb, eatNext = true))
                      case TOKUSHU_DA =>
                        if (following.getAllFeaturesArray()(CFORM).equals(TAIGENSETSUZOKU)) {
                          Some(TokenParseActions(pos = Pos.Adjective, eatNext = true, eatLemma = false))
                        } else {
                          Some(TokenParseActions(pos = Pos.Adjective))
                        }
                      case TOKUSHU_NAI => Some(TokenParseActions(pos = Pos.Adjective, eatNext = true))
                      case _ => if (followingPOSArray(POS1)
                        .equals(JOSHI))
                        Some(TokenParseActions(pos = Pos.Adverb)) else Some(TokenParseActions(pos = Pos.Noun))

                    }
                  }
                case HIJIRITSU | TOKUSHU =>
                  if (currentPOSArray(POS3).equals(NO_DATA) || index == tokenSeq.length - 1)
                    Some(TokenParseActions(pos = Pos.Noun)) else {
                    val following = tokenSeq(index + 1)
                    val followingPOSArray = util.Arrays.copyOfRange(following.getAllFeaturesArray, POS1, POS4 + 1)
                    currentPOSArray(POS3) match {
                      case FUKUSHIKANOU =>
                        if (followingPOSArray(POS1).equals(JOSHI) &&
                          following.getSurface.equals(NI)) Some(TokenParseActions(pos = Pos.Adverb, eatNext = true)) else
                          Some(TokenParseActions(pos = Pos.Noun))

                      case JODOUSHIGOKAN =>
                        if (following.getAllFeaturesArray()(CTYPE).equals(TOKUSHU_DA))
                          Some(TokenParseActions(pos = Pos.Verb, grammar = Grammar.Auxiliary,
                            eatNext = following.getAllFeaturesArray()(CFORM).equals(TAIGENSETSUZOKU))) else
                          if (followingPOSArray(POS1).equals(JOSHI) &&
                          followingPOSArray(POS2).equals(FUKUSHIKA))
                            Some(TokenParseActions(pos = Pos.Adverb, eatNext = true))
                          else Some(TokenParseActions(pos = Pos.Noun))

                      case KEIYOUDOUSHIGOKAN =>
                        Some(TokenParseActions(pos = Pos.Adjective,
                          eatNext = following.getAllFeaturesArray()(CTYPE).equals(TOKUSHU_DA) &&
                            following.getAllFeaturesArray()(CFORM).equals(TAIGENSETSUZOKU) ||
                              followingPOSArray(POS2).equals(RENTAIKA)))

                      case _ => Some(TokenParseActions(pos = Pos.Noun))
                    }
                  }
                case KAZU =>
                  if (parsedWords.nonEmpty && parsedWords(finalSlot).partOfSpeech.equals(Pos.Number))
                    Some(TokenParseActions(pos = Pos.Number, attachToPrevious = true, alsoAttachToLemma = true))
                    else Some(TokenParseActions(pos = Pos.Number))

                case SETSUBI =>
                  if (currentPOSArray(POS3).equals(JINMEI)) Some(TokenParseActions(pos = Pos.Suffix)) else
                    if (currentPOSArray(POS3).equals(TOKUSHU) && currentToken.getAllFeaturesArray()(BASIC).equals(SA))
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
            if (lastToken.isEmpty || !util.Arrays.copyOfRange(lastToken.get.getAllFeaturesArray,
              POS1, POS4 + 1)(POS2).equals(KAKARIJOSHI) &&
              qualifyingList.contains(currentToken.getAllFeaturesArray()(CTYPE)))
              Some(TokenParseActions(pos = defaultPos, attachToPrevious = true))
            else if (currentToken.getAllFeaturesArray()(CTYPE).equals(FUHENKAGATA) &&
              currentToken.getAllFeaturesArray()(BASIC).equals(NN))
              Some(TokenParseActions(pos = defaultPos, attachToPrevious = true))
            else if ((currentToken.getAllFeaturesArray()(CTYPE).equals(TOKUSHU_DA) ||
              currentToken.getAllFeaturesArray()(CTYPE).equals(TOKUSHU_DESU)) && !currentToken.getSurface.equals(NA))
              Some(TokenParseActions(pos = Pos.Verb))
            else Some(TokenParseActions(pos = defaultPos))

          case DOUSHI =>
            val pos = Pos.Verb
            currentPOSArray(POS2) match {
              case SETSUBI => Some(TokenParseActions(pos = pos, attachToPrevious = true))
              case HIJIRITSU => if (!currentToken.getAllFeaturesArray()(CFORM).equals(MEIREI_I))
                Some(TokenParseActions(pos = pos, attachToPrevious = true))
                else Some(TokenParseActions(pos = pos))
              case _ => Some(TokenParseActions(pos = pos))
            }
          case KEIYOUSHI => Some(TokenParseActions(pos = Pos.Adjective))
          case JOSHI =>
            val qualifyingList = Seq(TE, DE, BA)
            if (currentPOSArray(POS2).equals(SETSUZOKUJOSHI) && qualifyingList.contains(currentToken.getSurface))
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

        val updatedWords: Seq[Word] = actions match {
          case Some(parsedActions) => if (parsedActions.attachToPrevious && parsedWords.nonEmpty) {
            parsedWords match {
              case init :+ last =>
                val baseAttachedPrevious: Word = last.copy(tokens = last.tokens :+ currentToken)
                  .withAppendToWord(currentToken.getSurface)
                  .withAppendToReading(getFeatureSafely(currentToken, READING))
                  .withAppendToTranscription(getFeatureSafely(currentToken, PRONUNCIATION))
                val withAttachedLemma: Word = if (parsedActions.alsoAttachToLemma)
                  baseAttachedPrevious.withAppendToLemma(currentToken.getAllFeaturesArray()(BASIC)) else baseAttachedPrevious
                val withUpdatePos: Word = if (parsedActions.updatePos) withAttachedLemma.copy(partOfSpeech = parsedActions.pos) else withAttachedLemma
                init :+ withUpdatePos
              case _ => Nil
            }
          } else {
            val word = Word(reading = Option(currentToken.getReading),
              transcription = Option(getFeatureSafely(currentToken, PRONUNCIATION)),
              grammar = parsedActions.grammar,
              lemma = Option(currentToken.getAllFeaturesArray()(BASIC)),
              partOfSpeech = parsedActions.pos,
              word = currentToken.getSurface,
              tokens = Seq(currentToken))
            val withEatNext: Word = if (parsedActions.eatNext) {
              if (index == tokenSeq.length - 1) throw new IllegalStateException("There's a path that allows array overshooting.")
              val following: Token = tokenSeq(index + 1)
              val withMost: Word = word.copy(tokens = word.tokens :+ following)
                .withAppendToWord(following.getSurface)
                .withAppendToReading(following.getReading)
                .withAppendToTranscription(getFeatureSafely(following, PRONUNCIATION))
              if (parsedActions.eatLemma) withMost.withAppendToLemma(following.getAllFeaturesArray()(BASIC)) else withMost
            } else word
            parsedWords :+ withEatNext
          }
          case None => parsedWords
        }

        (updatedWords, Some(currentToken), actions)
    }
    words
  }

  private def getFeatureSafely(token: Token, feature: Int): String = {
    if (feature > PRONUNCIATION) throw new IllegalStateException("Asked for a feature out of bounds.")
    if (token.getAllFeaturesArray.length >= feature + 1 ) token.getAllFeaturesArray()(feature) else "*"
  }

  private case class TokenParseActions(pos: Pos.Value = Pos.TBD, eatNext: Boolean = false, eatLemma: Boolean = true,
                                       attachToPrevious: Boolean = false, alsoAttachToLemma: Boolean = false,
                                       updatePos: Boolean = false, grammar: Grammar.Value = Grammar.Unassigned)

  // POS1
  private val MEISHI = "名詞"
  private val KOYUUMEISHI = "固有名詞"
  private val DAIMEISHI = "代名詞"
  private val JODOUSHI = "助動詞"
  private val KAZU = "数"
  private val JOSHI = "助詞"
  private val SETTOUSHI = "接頭詞"
  private val DOUSHI = "動詞"
  private val KIGOU = "記号"
  private val FIRAA = "フィラー"
  private val SONOTA = "その他"
  private val KANDOUSHI = "感動詞"
  private val RENTAISHI = "連体詞"
  private val SETSUZOKUSHI = "接続詞"
  private val FUKUSHI = "副詞"
  private val SETSUZOKUJOSHI = "接続助詞"
  private val KEIYOUSHI = "形容詞"
  @unused
  private val MICHIGO = "未知語"

  // POS2_BLACKLIST and inflection types
  private val HIJIRITSU = "非自立"
  private val FUKUSHIKANOU = "副詞可能"
  private val SAHENSETSUZOKU = "サ変接続"
  private val KEIYOUDOUSHIGOKAN = "形容動詞語幹"
  private val NAIKEIYOUSHIGOKAN = "ナイ形容詞語幹"
  private val JODOUSHIGOKAN = "助動詞語幹"
  private val FUKUSHIKA = "副詞化"
  private val TAIGENSETSUZOKU = "体言接続"
  private val RENTAIKA = "連体化"
  private val TOKUSHU = "特殊"
  private val SETSUBI = "接尾"
  private val SETSUZOKUSHITEKI = "接続詞的"
  private val DOUSHIHIJIRITSUTEKI = "動詞非自立的"
  private val SAHEN_SURU = "サ変・スル"
  private val TOKUSHU_TA = "特殊・タ"
  private val TOKUSHU_NAI = "特殊・ナイ"
  private val TOKUSHU_TAI = "特殊・タイ"
  private val TOKUSHU_DESU = "特殊・デス"
  private val TOKUSHU_DA = "特殊・ダ"
  private val TOKUSHU_MASU = "特殊・マス"
  private val TOKUSHU_NU = "特殊・ヌ"
  private val FUHENKAGATA = "不変化型"
  private val JINMEI = "人名"
  private val MEIREI_I = "命令ｉ"
  private val KAKARIJOSHI = "係助詞"
  @unused
  private val KAKUJOSHI = "格助詞"

  // etc
  private val NA = "な"
  private val NI = "に"
  private val TE = "て"
  private val DE = "で"
  private val BA = "ば"
  private val NN = "ん"
  private val SA = "さ"
}
