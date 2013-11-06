package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.event.OrderReplayEvent
import java.io.PrintStream

/**
 * Super-class of all order replay classes.  These classes replay events through a simulator
 * in order to reconstruct the state of the market at a given point in time and collect
 * data on the market state, writing it out to a time-series.
 *
 * (c) Steve Phelps 2013
 */

abstract class AbstractOrderReplay(val withGui: Boolean = false, val outFileName: Option[String] = None)
    extends Iterable[OrderReplayEvent] {

  val out = outFileName match {
    case Some(fileName) => {
      val outFile = new java.io.FileOutputStream(outFileName.get)
      new PrintStream(outFile)
    }
    case None => System.out
  }

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
      out.println(t.get.getTicks + "\t" + (price match {
        case Some(p) => p.toString()
        case None => "NaN"
      }))
    }
    Unit
  }
}
