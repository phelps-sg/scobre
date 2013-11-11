package org.ccfea.tickdata.simulator

/**
 * (C) Steve Phelps 2013
 */
trait MarketStateDataCollector[T] extends OrderReplayer[T] {

  def collectData(state: MarketState): T

  def replayEvents: Iterable[T] = {
    simulator.map(collectData)
  }
}
