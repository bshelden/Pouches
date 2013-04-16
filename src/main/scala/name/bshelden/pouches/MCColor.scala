package name.bshelden.pouches

/**
 * ADT for colors
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
sealed trait MCColor {
  val id: Int
  val name: String
  val oreDict: String
}

object MCColor {
  private def mk(i: Int, n: String, od: String): MCColor = new MCColor {
    val id = i
    val name = n
    val oreDict = od
  }

  val Black     = mk(0 , "black",     "dyeBlack"    )
  val Red       = mk(1 , "red",       "dyeRed"      )
  val Green     = mk(2 , "green",     "dyeGreen"    )
  val Brown     = mk(3 , "brown",     "dyeBrown"    )
  val Blue      = mk(4 , "blue",      "dyeBlue"     )
  val Purple    = mk(5 , "purple",    "dyePurple"   )
  val Cyan      = mk(6 , "cyan",      "dyeCyan"     )
  val Silver    = mk(7 , "silver",    "dyeLightGray")
  val Gray      = mk(8 , "gray",      "dyeGray"     )
  val Pink      = mk(9 , "pink",      "dyePink"     )
  val Lime      = mk(10, "lime",      "dyeLime"     )
  val Yellow    = mk(11, "yellow",    "dyeYellow"   )
  val LightBlue = mk(12, "lightBlue", "dyeLightBlue")
  val Magenta   = mk(13, "magenta",   "dyeMagenta"  )
  val Orange    = mk(14, "orange",    "dyeOrange"   )
  val White     = mk(15, "white",     "dyeWhite"    )

  val all = Seq(
    Black,     Red,     Green,  Brown,
    Blue,      Purple,  Cyan,   Silver,
    Gray,      Pink,    Lime,   Yellow,
    LightBlue, Magenta, Orange, White)
}
