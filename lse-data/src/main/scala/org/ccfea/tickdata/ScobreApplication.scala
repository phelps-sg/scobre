package org.ccfea.tickdata

import java.text.DateFormat
import java.util.Date

import net.sourceforge.jasa.market.Price
import org.ccfea.tickdata.conf.ReplayConf
import org.ccfea.tickdata.simulator.{ClearingMarketState, MarketState}

import scala.collection.immutable.ListMap

/**
  * Created by sphelps on 06/05/16.
  */
trait ScobreApplication {

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
    case Some(price: Price) => Some(price.doubleValue)
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
    * @return  Either a MarketState or ClearingMarketState depending on the
    *          command-line options.
    */
  def newMarketState(conf: ReplayConf) =
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
}
