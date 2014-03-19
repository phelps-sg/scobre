package org.ccfea.tickdata.simulator

import org.ccfea.tickdata.event.{OrderReplayEvent, Event}
import java.util.{Observable, Observer}

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
class MarketSimulator(val events: Iterable[OrderReplayEvent], val market: MarketState = new MarketState())
    extends Observable {

  this.addObserver(market)

  def map[B](f: MarketState => B): Iterable[B] = {
    events.map(ev => {
      process(ev)
      f(market)
    })
  }

  def process(ev: OrderReplayEvent) = {
    setChanged()
    notifyObservers(ev)
  }

  def step() = {
    val ev = events.iterator.next()
    process(ev)
  }

}

