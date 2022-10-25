package com.megafarad.ve_scala

object Grammar extends Enumeration {
  type Grammar = Value
  val Auxiliary,
      Nominal,
      Comparative,
      Superlative,
      Modal,
      Plural,
      Personal,
      Possessive,
      Past,
      PresentParticiple,
      PastParticiple,
      Unassigned = Value
}
