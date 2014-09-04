package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime

import scala.collection.immutable.HashMap

/**
 * (C) Steve Phelps 2014
 */
trait MultivariateTimeSeriesCollector
    extends MarketStateDataCollector[(Option[SimulationTime], Map[MarketState => Option[AnyVal], Option[AnyVal]])] {

  val dataCollectors: Seq[MarketState => Option[AnyVal]]

//  def collectData(state: MarketState) = {
//    new HashMap(for(collector <- dataCollectors) yield collector -> collector(state))
//  }

}
