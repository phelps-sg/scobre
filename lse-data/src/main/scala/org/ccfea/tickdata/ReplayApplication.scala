package org.ccfea.tickdata

import java.text.DateFormat
import java.util.Date

import org.ccfea.tickdata.conf.ReplayConf
import org.ccfea.tickdata.simulator.{MarketState, ClearingMarketState}

/**
 * (C) Steve Phelps 2014
 */
trait ReplayApplication {

  def parseDate(date: Option[String]): Option[Date] = date match {
    case None => None
    case Some(dateStr) =>  Some(DateFormat.getDateInstance(DateFormat.SHORT).parse(dateStr))
  }

  implicit def AnyRefToOptionAnyVal(x: AnyRef): Option[AnyVal] = x match {
    case Some(double: Double) => Some(double)
    case Some(long: Long) => Some(long)
    case None => None
  }

  def newMarketState(implicit conf: ReplayConf) =
    if (conf.explicitClearing()) new ClearingMarketState() else new MarketState()

  def main(args:Array[String])

}
