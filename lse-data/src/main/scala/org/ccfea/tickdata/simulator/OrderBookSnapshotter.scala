package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import java.util.Date
import net.sourceforge.jasa.market.FourHeapOrderBook
import org.ccfea.tickdata.storage.csv.{PrintStreamOutputer, UnivariateCsvDataCollator}

import collection.JavaConversions._
import util.control.Breaks._
import grizzled.slf4j.Logger
import java.util
import org.ccfea.tickdata.event.TickDataEvent

/**
 * (C) Steve Phelps 2013
 */
class OrderBookSnapshotter(val eventSource: Iterable[TickDataEvent], val t: SimulationTime,
                            val outFileName: Option[String] = None, val withGui: Boolean = false,
                            val marketState: MarketState = new MarketState())
    extends OrderReplayer[Option[FourHeapOrderBook]] with PrintStreamOutputer {

  val logger = Logger(classOf[OrderBookSnapshotter])

  val targetDate = new java.util.Date(t.getTicks)
  logger.debug("Snapshot target date = " + targetDate)

  override val simulator =
    new MarketSimulator(eventSource.takeWhile(_.timeStamp.compareTo(targetDate) >= 0).take(1), marketState)

  def replayEvents(): Iterable[Option[FourHeapOrderBook]] = {
    for (state <- simulator) yield Some(state.book)
  }

  def qty(order: Option[net.sourceforge.jasa.market.Order]) = order match {
    case Some(o) => o.getQuantity
    case None => 0
  }

  def price(order: Option[net.sourceforge.jasa.market.Order]) = order match {
    case Some(o) => o.getPrice
    case None => Double.NaN
  }

  def outputResult(result: Iterable[Option[FourHeapOrderBook]]) {
    val out = openOutput()
    logger.debug("book = " + result)
    val book = result.iterator.next().get
    val asks = book.getUnmatchedAsks
    val bids = book.getUnmatchedBids
    val levels = Math.max(asks.size, bids.size)
    for(i <- 0 to levels-1) {
      val ask = if (i < asks.size) Some(asks.get(i)) else None
      val bid = if (i < bids.size) Some(bids.get(i)) else None
      out.println(qty(ask) + "\t" + price(ask) + "\t" + qty(bid) + "\t" + price(bid))
    }
    out.close()
  }

}
