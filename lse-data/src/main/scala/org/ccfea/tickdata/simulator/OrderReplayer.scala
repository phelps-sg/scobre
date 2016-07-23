package org.ccfea.tickdata.simulator

import org.ccfea.tickdata.event.TickDataEvent

/**
 * Super-class of all order replay classes.  These classes replay events through a simulator
 * in order to reconstruct the state of the market at a given point in time and collect
 * data on the market state, writing it out to a time-series.
 *
 * (c) Steve Phelps 2015
 */
trait OrderReplayer[T] extends Runnable {

  /**
   * The source of event objects to replay.
   */
  def eventSource: Iterable[TickDataEvent]

  /**
   * The initial market state.
   *
   * @return  A mutable representation of the state of the market,
   *            which will change over time as subsequent ticks are replayed.
   */
  def marketState: MarketState

  val simulator = new MarketSimulator(eventSource, marketState)

  /**
   * Run the replay which will simulate the events and collate the resulting data.
   */
  def run() {
    val data = replayEvents()
    outputResult(data)
  }

  /**
   * Run a single step of the simulation.
   */
  def step() {
    simulator.step()
  }

  /**
   * Replay all events through the simulator.
   *
   * @return  An Iterable over the data collected from the simulation.
   */
  def replayEvents(): Iterable[T]

  /**
   * Record the collated variables to a file.
   *
   * @param data  An iterable collection of values collated from the state of the market.
   */
  def outputResult(data: Iterable[T])

}
