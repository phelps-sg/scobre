package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import java.util.Date
import net.sourceforge.jasa.market.FourHeapOrderBook

import collection.JavaConversions._
import util.control.Breaks._

/**
 * (C) Steve Phelps 2013
 */
abstract class OrderBookSnapshotter extends OrderReplayer[Option[FourHeapOrderBook]] {

  def t: SimulationTime

  def outputResult(result: Iterable[Option[FourHeapOrderBook]]) {
    //TODO
    val book = result.iterator.next().get
    for(ask <- book.askIterator()) {
      out.println(ask.getQuantity + "\t" + ask.getPrice)
    }
  }

  def replayEvents: Iterable[Option[FourHeapOrderBook]] = {
    for (state <- simulator; time = state.time) {
      if (time.get.compareTo(t) >=0) {
        return List( Some(state.book) )
      } else {
        println(time)
      }
    }
    return List()
  }

}
