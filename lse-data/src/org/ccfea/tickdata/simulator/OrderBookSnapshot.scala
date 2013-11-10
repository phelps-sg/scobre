package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import java.util.Date
import net.sourceforge.jasa.market.FourHeapOrderBook

import collection.JavaConversions._

/**
 * (C) Steve Phelps 2013
 */
abstract class OrderBookSnapshot extends OrderReplayer {

  def time: Date

  override def run = {
    val book: FourHeapOrderBook = takeSnapshots(new SimulationTime(time.getTime)).iterator.next()
    save(book)
  }

  def save(book: FourHeapOrderBook) {
    for(ask <- book.askIterator()) {
      out.println(ask.getQuantity + "\t" + ask.getPrice)
    }
  }

  def simultaneous(t1: SimulationTime, t2: SimulationTime, resolution:Int = 5000): Boolean = {
    (t1.getTicks / resolution) == (t2.getTicks / resolution)
  }

  def takeSnapshots(t: SimulationTime) = {
    for {
      state <- simulator;
      time = state.time;
      if (time match {
        case None => false
        case Some(now) => simultaneous(t, now)
      })
    } yield state.book
  }

}
