package org.ccfea.tickdata.simulator

/**
 * A quote in the market.
 *
 * (C) Steve Phelps 2013
 */
case class Quote(val bid: Option[Double], val ask: Option[Double])
