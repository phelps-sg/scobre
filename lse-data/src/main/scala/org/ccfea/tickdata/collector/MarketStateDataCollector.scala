package org.ccfea.tickdata.collector

import org.ccfea.tickdata.simulator.{MarketState, OrderReplayer}

/**
 * Collect data on the state of the market by replaying events.
 *
 * (C) Steve Phelps 2013
 */
trait MarketStateDataCollector[T] extends OrderReplayer[T] {

  /**
   * A function for collecting data of interest from the
   * state of the market.
   *
   * @param state  The state of the market at a single moment in time.
   * @return       A measurement based on the state of the market.
   */
  def collectData(state: MarketState): T

  def replayEvents(): Iterable[T] = {
    for(marketState <- simulator) yield collectData(marketState)
  }

}
