package org.ccfea.tickdata

import java.util
import org.ccfea.tickdata.conf.ReplayConf
import org.ccfea.tickdata.simulator.{ClearingMarketState, MarketState}

import collection.JavaConversions._

import scala.collection.immutable.ListMap

/**
 * Common functionality for all applications which replay tick events and collate data
 * on the evolving market state.
 *
 * (C) Steve Phelps 2015
 */
trait ReplayApplication extends ScobreApplication {

  implicit def AnyRefToOptionAnyVal(x: AnyRef): Option[AnyVal] = x match {
    case Some(double: Double) => Some(double)
    case Some(long: Long) => Some(long)
    case Some(int: Int) => Some(int)
    case Some(bd: BigDecimal) => Some(bd.doubleValue())
    case l: java.lang.Long => Some(l.longValue())
    case d: java.lang.Double => Some(d.doubleValue())
    case Some(ch: Char) => Some(ch)
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
    if (conf.explicitClearing()) new ClearingMarketState() else new MarketState()

  /**
   *    Use reflection to find the method to retrieve the  data for each variable (a function of MarketState).
   *
   * @param variables  The variables to collect from the simulation.
   * @return            a map of variables and methods, i.e. the collectors for the simulation.
   */
  def dataCollectors(variables: List[String]): Map[String, MarketState => Option[AnyVal]] = {
    def collector(variable: String): MarketState => Option[AnyVal] =
      classOf[MarketState].getMethod(variable) invoke _
    ListMap() ++ variables.map(variable => (variable, collector(variable)))
  }

  def main(args:Array[String])

}
