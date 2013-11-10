package org.ccfea.tickdata.simulator

/**
 * (C) Steve Phelps 2013
 */
trait MarketStateDataCollector extends OrderReplayer {

  def collectData(state: MarketState): Any

  def replayEvents: Any = {
    simulator.map(collectData)
  }
}
