package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.event.OrderReplayEvent
import java.io.PrintStream
import java.util.Date
import org.ccfea.tickdata.cep.CepMarketSimulator

/**
 * Super-class of all order replay classes.  These classes replay events through a simulator
 * in order to reconstruct the state of the market at a given point in time and collect
 * data on the market state, writing it out to a time-series.
 *
 * (c) Steve Phelps 2013
 */

trait OrderReplayer[T] extends Runnable {

  val out: java.io.PrintStream = openOutput

  def withGui: Boolean
  def outFileName: Option[String]
  def eventSource: Iterable[OrderReplayEvent]

  val marketState = if (withGui) new MarketStateWithGUI() else new MarketState()
  val simulator = new CepMarketSimulator(eventSource, marketState)

  def openOutput() = outFileName match {
    case Some(fileName) => {
      val outFile = new java.io.FileOutputStream(outFileName.get)
      new PrintStream(outFile)
    }
    case None => System.out
  }

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

  def replayEvents(): Iterable[T]

  def outputResult(data: Iterable[T])

}
