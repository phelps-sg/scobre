package org.ccfea.tickdata

/**
 * A simulator which takes a sequence of events and replays them, producing a MarketState object for each event.
 *
 * (c) Steve Phelps 2013
 */
class MarketSimulator(val events: Iterable[Event], val market: MarketState = new MarketState()) {

  def map[B](f: MarketState => B): Iterable[B] = {
    events.map(ev => {
      market.processEvent(ev)
      f(market)
    })
  }

}

