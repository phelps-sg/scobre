package org.ccfea.tickdata.simulator

/**
 * Collect data on the state of the market by replaying events.
 *
 * (C) Steve Phelps 2013
 */
trait MarketStateDataCollector[T] extends OrderReplayer[T] {

  def collectData(state: MarketState): T

  def replayEvents(): Iterable[T] = {
    for(marketState <- simulator) yield collectData(marketState)
  }

}
