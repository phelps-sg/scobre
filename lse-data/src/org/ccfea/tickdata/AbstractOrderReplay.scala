package org.ccfea.tickdata

import net.sourceforge.jabm.SimulationTime

/**
 * Super-class of all order replay classes.  These classes replay events through a simulator
 * in order to reconstruct the state of the market at a given point in time and collect
 * data on the market state, writing it out to a time-series.
 *
 * (c) Steve Phelps 2013
 */

abstract class AbstractOrderReplay(val withGui: Boolean = false) extends Iterable[Event] {

  def run {
    val timeSeries = replayEvents
    outputTimeSeries(timeSeries)
  }

  def replayEvents = {
    val marketState = if (withGui) new MarketStateWithGUI() else new MarketState()
    val simulator = new MarketSimulator(this, marketState)
    for {
      state <- simulator
    } yield (state.time, state.midPrice)
  }

  def outputTimeSeries(timeSeries: Iterable[(Option[SimulationTime], Option[Double])]) {
    for ((t, price) <- timeSeries) {
      println(t.get.getTicks + "\t" + (price match {
        case Some(p) => p.toString()
        case None => "NaN"
      }))
    }
    Unit
  }
}
