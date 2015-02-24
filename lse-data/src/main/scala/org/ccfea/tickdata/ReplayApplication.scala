package org.ccfea.tickdata

import java.text.DateFormat
import java.util.Date

import org.ccfea.tickdata.conf.ReplayConf
import org.ccfea.tickdata.simulator.{MarketState, ClearingMarketState}

/**
 * Common functionality for all applications which replay tick events and collate data
 * on the evolving market state.
 *
 * (C) Steve Phelps 2015
 */
trait ReplayApplication {

  /**
   * Parse a date supplied as a command-line option.
   *
   * @param date  An optional date in the standard java short format.
   *
   * @return  An optional java.util.Date instance.
   */
  def parseDate(date: Option[String]): Option[Date] = date match {
    case None => None
    case Some(dateStr) =>  Some(DateFormat.getDateInstance(DateFormat.SHORT).parse(dateStr))
  }

  implicit def AnyRefToOptionAnyVal(x: AnyRef): Option[AnyVal] = x match {
    case Some(double: Double) => Some(double)
    case Some(long: Long) => Some(long)
    case None => None
  }

  /**
   * Construct an initial market state.  This is a factory method
   * which will create an appropriate class of market-state depending
   * on command-line options controlling the choice of clearing rules.
   *
   * @param conf   The command-line options
   * @return  Either a MarketState or ClearingMarketState depending on the
   *          command-line options.
   */
  def newMarketState(implicit conf: ReplayConf) =
    if (conf.explicitClearing()) new MarketState() else new ClearingMarketState()

  def main(args:Array[String])

}
