package org.ccfea.tickdata.simulator

import org.ccfea.tickdata.event.TickDataEvent

import scala.collection.mutable.Publisher

/**
 * A simulator which takes a sequence of events and replays them, producing some function of a
 * MarketState object for each event via a map method.  The specified function can be used to collect data of interest
 * on the market, e.g. the mid-price, as a time-series.  By implementing a map method, instances of this class
 * can be used in for comprehensions; e.g.
 *
 * <code>
 *  val simulator = new MarketSimulator(events, market)
 *  val prices = for(state <- simulator) yield(state.midPrice)
 * </code>
 *
 * (c) Steve Phelps 2013
 */
class MarketSimulator(val events: Iterable[TickDataEvent], val market: MarketState = new MarketState())
    extends Publisher[TickDataEvent] {

  subscribe(market)

  //TODO: Add filters to ignore outliers

  def map[B](f: MarketState => B): Iterable[B] = {
    events.map(ev => {
      process(ev)
      f(market)
    })
  }

  def process(ev: TickDataEvent) = {
    publish(ev)
  }

  def step() = {
    val ev = events.iterator.next()
    process(ev)
  }

}

