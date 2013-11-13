package org.ccfea.tickdata.simulator

import org.ccfea.tickdata.event.{OrderReplayEvent, Event}

/**
 * A simulator which takes a sequence of events and replays them, producing some function of a
 * MarketState object for each event.  The specified function can be used to collect data of interest
 * on the market, e.g. the mid-price, as a time-series.
 *
 * (c) Steve Phelps 2013
 */
class MarketSimulator(val events: Iterable[OrderReplayEvent], val market: MarketState = new MarketState()) {

  def map[B](f: MarketState => B): Iterable[B] = {
    events.map(ev => {
      market.newEvent(ev)
      f(market)
    })
  }

  def step() = {
    market.newEvent(events.iterator.next())
  }

}

