package org.ccfea.tickdata.simulator

import org.ccfea.tickdata.event.TickDataEvent

import scala.collection.mutable.Publisher

/**
 * A simulator which takes a sequence of events and replays them, producing some function of a
 * MarketState object for each event via a map method.  The specified function can be used to collect data of interest
 * on the market, e.g. the mid-price, as a time-series.  Instances of this class can be used in for comprehensions;
 *  e.g.
 *
 * <code>
 *  val simulator = new MarketSimulator(events, market)
 *  val prices = for(state <- simulator) yield(state.midPrice)
 * </code>
 *
 * (c) Steve Phelps 2013
 */
class MarketSimulator(val ticks: Iterable[TickDataEvent], val market: MarketState = new MarketState())
    extends Publisher[TickDataEvent] {

  subscribe(market)

  val realTicksIterator = ticks.iterator

  /**
   * Create a tick iterator which merges virtual ticks with real ticks.
   * Virtual ticks take priority and are returned first until the virtual tick iterator is depleted.
   * @return
   */
  def tickIterator(): Iterator[TickDataEvent] = {
    val virtualTicksIterator = market.virtualTicks.iterator
    new Iterator[TickDataEvent] {
      def hasNext = virtualTicksIterator.hasNext || realTicksIterator.hasNext
      def next() = if (virtualTicksIterator.hasNext) virtualTicksIterator.next else realTicksIterator.next
    }
  }

  def map[B](f: MarketState => B): Iterable[B] = {
    val it = tickIterator()
    new Iterable[B] {
      def iterator: Iterator[B] = new Iterator[B] {
        def hasNext = it.hasNext
        def next() = {
          process(it.next())
          f(market)
        }
      }
    }
  }

  def process(tick: TickDataEvent) = {
    publish(tick)
  }

  def step() = {
    val tick = ticks.iterator.next()
    process(tick)
  }

}
