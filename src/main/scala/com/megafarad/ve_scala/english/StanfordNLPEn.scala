package com.megafarad.ve_scala.english

import com.megafarad.ve_scala.{Grammar, Pos, Parse}
import edu.stanford.nlp.simple._

import scala.jdk.CollectionConverters._

class StanfordNLPEn(tokens: Seq[EnglishToken]) extends Parse{

  def this(text: String) = {
    this(new Document(text).sentences().asScala.toSeq.flatMap {
      sentence =>
        sentence.tokens().asScala.toSeq.zipWithIndex.foldLeft[Seq[EnglishToken]](Nil) {
          case (sentenceTokens, (token: Token, idx: Int)) =>
            sentenceTokens :+ EnglishTokenParser.parse(token, idx == sentence.tokens().size() - 1)
        }
    })
  }

  override def words: Seq[EnglishWord] = {
    tokens.foldLeft[Seq[EnglishWord]](Nil) {
      (parsedWords, token) =>
        if (token.pos.equals("POS")) {
          parsedWords match {
            case words :+ lastWord => words :+ lastWord.copy(word = lastWord.word.concat(token.literal),
              tokens = lastWord.tokens :+ token)
            case _ => Seq(EnglishWord(lemma = token.lemma,
              partOfSpeech = Pos.Unknown,
              grammar = Grammar.Unassigned,
              word = token.literal,
              tokens = Seq(token)))
          }
        } else  {
          val (lookedUpPOS, grammar) = posMap.getOrElse(token.pos, (Pos.Unknown, Grammar.Unassigned))
          val pos = if (token.pos.equals(".")) Pos.Symbol else lookedUpPOS
          parsedWords :+ EnglishWord(lemma = token.lemma, partOfSpeech = pos, grammar = grammar,
            word = token.literal, tokens = Seq(token))
        }
    }
  }


  private val posMap: Map[String, (Pos.Value, Grammar.Value)] = Map(
    "CC" -> (Pos.Conjunction, Grammar.Unassigned),
    "CD" -> (Pos.Number, Grammar.Unassigned),
    "DT" -> (Pos.Determiner, Grammar.Unassigned),
    "EX" -> (Pos.Pronoun, Grammar.Unassigned),
    "FW" -> (Pos.Unknown, Grammar.Unassigned),
    "IN" -> (Pos.Preposition, Grammar.Unassigned),
    "JJ" -> (Pos.Adjective, Grammar.Unassigned),
    "JJR" -> (Pos.Adjective, Grammar.Comparative),
    "JJS" -> (Pos.Adjective, Grammar.Superlative),
    "LS" -> (Pos.Unknown, Grammar.Unassigned),
    "MD" -> (Pos.Verb, Grammar.Modal),
    "NN" -> (Pos.Noun, Grammar.Unassigned),
    "NNS" -> (Pos.Noun, Grammar.Plural),
    "NNP" -> (Pos.ProperNoun, Grammar.Unassigned),
    "NNPS" -> (Pos.ProperNoun, Grammar.Plural),
    "PDT" -> (Pos.Determiner, Grammar.Unassigned),
    "PRP" -> (Pos.Pronoun, Grammar.Personal),
    "PRP$" -> (Pos.Pronoun, Grammar.Possessive),
    "RB" -> (Pos.Adverb, Grammar.Unassigned),
    "RBR" -> (Pos.Adverb, Grammar.Comparative),
    "RBS" -> (Pos.Adverb, Grammar.Superlative),
    "RP" -> (Pos.Postposition, Grammar.Unassigned),
    "SYM" -> (Pos.Symbol, Grammar.Unassigned),
    "TO" -> (Pos.Preposition, Grammar.Unassigned),
    "UH" -> (Pos.Interjection, Grammar.Unassigned),
    "VB" -> (Pos.Verb, Grammar.Unassigned),
    "VBD" -> (Pos.Verb, Grammar.Past),
    "VBG" -> (Pos.Verb, Grammar.PresentParticiple),
    "VBN" -> (Pos.Verb, Grammar.PastParticiple),
    "VBP" -> (Pos.Verb, Grammar.Unassigned),
    "VBZ" -> (Pos.Verb, Grammar.Unassigned),
    "WDT" -> (Pos.Determiner, Grammar.Unassigned),
    "WP" -> (Pos.Pronoun, Grammar.Unassigned),
    "WP$" -> (Pos.Pronoun, Grammar.Possessive),
    "WRB" -> (Pos.Adverb, Grammar.Unassigned),
    "Z" -> (Pos.Determiner, Grammar.Unassigned)
  )

}
