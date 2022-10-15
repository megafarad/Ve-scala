package com.megafarad.ve_scala

object Grammar extends Enumeration {
  type Grammar = Value
  val Auxiliary,
      Nominal,
      Unassigned = Value
}
