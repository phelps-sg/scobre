package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import java.util.Date
import net.sourceforge.jasa.market.FourHeapOrderBook

import collection.JavaConversions._
import util.control.Breaks._

/**
 * (C) Steve Phelps 2013
 */
abstract class OrderBookSnapshotter extends OrderReplayer {

  def time: Date

  override def run = {
    val book: Option[FourHeapOrderBook] = takeSnapshot(new SimulationTime(time.getTime))
    save(book.get)
  }

  def save(book: FourHeapOrderBook) {
    for(ask <- book.askIterator()) {
      out.println(ask.getQuantity + "\t" + ask.getPrice)
    }
  }

  def takeSnapshot(t: SimulationTime): Option[FourHeapOrderBook] = {
    for (state <- simulator; time = state.time) {
      if (time.get.compareTo(t) >=0) {
        return Some(state.book)
      } else {
        println(time)
      }
    }
    return None
  }

}
