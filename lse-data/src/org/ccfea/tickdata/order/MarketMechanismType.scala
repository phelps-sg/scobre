package org.ccfea.tickdata.order

/**
 * (c) Steve Phelps 2014
 */
object MarketMechanismType extends Enumeration {
  val LimitOrder = Value("LO")
  val MarketOrder = Value("MO")
  val Other = Value("OO")
}
