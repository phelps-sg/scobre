package org.ccfea.tickdata.collector

import grizzled.slf4j.Logger
import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.simulator.{AuctionState, MarketState}

import scala.collection.SortedMap

/**
 * (C) Steve Phelps 2014
 */
trait MultivariateTimeSeriesCollector
    extends MarketStateDataCollector[(Option[SimulationTime], SortedMap[String, Option[AnyVal]])] {

  /**
   * A mapping between names of variables and their corresponding data-collector functions.
   */
  val dataCollectors: Map[String, MarketState => Option[AnyVal]]

  def collectData(state: MarketState): (Option[SimulationTime], SortedMap[String, Option[AnyVal]]) = {
    val tuples = for((name, fn) <- dataCollectors) yield name -> fn(state)
//      if (state.auctionState == AuctionState.continuous) name -> fn(state) else name -> None
    (state.time, SortedMap[String, Option[AnyVal]]() ++ tuples)
  }

}
