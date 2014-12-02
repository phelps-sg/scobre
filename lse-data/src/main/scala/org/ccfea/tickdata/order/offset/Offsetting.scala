package org.ccfea.tickdata.order.offset

/**
 * (C) Steve Phelps 2014
 */
object Offsetting extends Enumeration {

  val NoOffsetting = Value(0)
  val SameSide = Value(1)
  val MidPrice = Value(2)
  val OppositeSide = Value(3)

}
