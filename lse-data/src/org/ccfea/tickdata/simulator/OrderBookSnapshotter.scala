package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import java.util.Date
import net.sourceforge.jasa.market.FourHeapOrderBook

import collection.JavaConversions._
import util.control.Breaks._
import grizzled.slf4j.Logger
import java.util

/**
 * (C) Steve Phelps 2013
 */
abstract class OrderBookSnapshotter extends OrderReplayer[Option[FourHeapOrderBook]] {

  def t: SimulationTime
  def logger: Logger

  def outputResult(result: Iterable[Option[FourHeapOrderBook]]) {
    logger.debug("book = " + result)
    val book = result.iterator.next().get
    val asks = book.getUnmatchedAsks
    val bids = book.getUnmatchedBids
    val levels = Math.max(asks.size, bids.size)
    def qty(order: Option[net.sourceforge.jasa.market.Order]) = order match {
      case Some(o) => o.getQuantity
      case None => 0
    }
    def price(order: Option[net.sourceforge.jasa.market.Order]) = order match {
      case Some(o) => o.getPrice
      case None => Double.NaN
    }
    for(i <- 0 to levels-1) {
      val ask = if (i < asks.size) Some(asks.get(i)) else None
      val bid = if (i < bids.size) Some(bids.get(i)) else None
      out.println(qty(ask) + "\t" + price(ask) + "\t" + qty(bid) + "\t" + price(bid))
    }
  }

  def replayEvents: Iterable[Option[FourHeapOrderBook]] = {
    logger.debug("Snapshot target time = " + t)
    for (state <- simulator; time = state.time; book = state.book) {
      if (time.get.compareTo(t) >=0) {
        logger.debug("Taking snapshot at " + new java.util.Date(time.get.getTicks))
        return List( Some(book) )
      } else {
        logger.debug(new java.util.Date(time.get.getTicks))
      }
    }
    return List()
  }

}
