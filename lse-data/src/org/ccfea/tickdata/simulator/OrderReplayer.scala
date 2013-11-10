package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.event.OrderReplayEvent
import java.io.PrintStream
import java.util.Date

/**
 * Super-class of all order replay classes.  These classes replay events through a simulator
 * in order to reconstruct the state of the market at a given point in time and collect
 * data on the market state, writing it out to a time-series.
 *
 * (c) Steve Phelps 2013
 */

trait OrderReplayer extends Iterable[OrderReplayEvent] with Runnable {

  val out: java.io.PrintStream = openOutput

  def withGui: Boolean
  def outFileName: Option[String]

  def openOutput = outFileName match {
    case Some(fileName) => {
      val outFile = new java.io.FileOutputStream(outFileName.get)
      new PrintStream(outFile)
    }
    case None => System.out
  }

  def run {
    val data = replayEvents
    outputResult(data)
  }

  def simulator = {
    val marketState = if (withGui) new MarketStateWithGUI() else new MarketState()
    new MarketSimulator(this, marketState)
  }

  def replayEvents: Any

  def outputResult(data: Any)

}
