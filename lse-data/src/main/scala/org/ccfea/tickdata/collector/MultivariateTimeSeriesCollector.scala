package org.ccfea.tickdata.collector

import grizzled.slf4j.Logger
import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.simulator.{AuctionState, MarketState}

/**
 * (C) Steve Phelps 2014
 */
trait MultivariateTimeSeriesCollector
    extends MarketStateDataCollector[(Option[SimulationTime], Map[String, Option[AnyVal]])] {

  /**
   * A mapping between names of variables and their corresponding data-collector functions.
   */
  val dataCollectors: Map[String, MarketState => Option[AnyVal]]

  def collectData(state: MarketState): (Option[SimulationTime], Map[String, Option[AnyVal]]) = {
    val tuples = for((name, fn) <- dataCollectors) yield
      if (state.auctionState == AuctionState.continuous) name -> fn(state) else name -> None
    (state.time, Map() ++ tuples)
  }

}
