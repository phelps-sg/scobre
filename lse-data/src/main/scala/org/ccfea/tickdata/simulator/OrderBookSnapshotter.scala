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
 * Take a snapshot of the order-book at the specified time, and record it to a CSV file.
 *
 * (C) Steve Phelps 2015
 */
class OrderBookSnapshotter(val eventSource: Iterable[TickDataEvent],
                            val outFileName: Option[String] = None,
                            val marketState: MarketState, val maxLevels:Int = 100)
  extends PrintStreamOutputer {

  val logger = Logger(classOf[OrderBookSnapshotter])

//  val targetDate = new java.util.Date(t.getTicks)
//  logger.debug("Snapshot target date = " + targetDate)

//  override val simulator =
//    new MarketSimulator(eventSource.takeWhile(_.timeStamp.compareTo(targetDate) >= 0).take(1), marketState)


  val out = openOutput()

  def replayEvents() {
    val simulator = new MarketSimulator(eventSource, marketState)
    val it = simulator.tickIterator()
    while (it.hasNext) {
      simulator.step()
      writeBook(marketState.book)
    }
  }

  def qty(order: Option[net.sourceforge.jasa.market.Order]) = order match {
    case Some(o) => o.getQuantity
    case None => 0
  }

  def price(order: Option[net.sourceforge.jasa.market.Order]) = order match {
    case Some(o) => o.getPrice
    case None => Double.NaN
  }

  def writeBook(book: FourHeapOrderBook) {
    val asks = book.getUnmatchedAsks
    val bids = book.getUnmatchedBids
    val levels = Math.max(maxLevels, Math.max(asks.size, bids.size))
    for (i <- 0 to levels - 1) {
      val ask = if (i < asks.size) Some(asks.get(i)) else None
      val bid = if (i < bids.size) Some(bids.get(i)) else None
      out.println(qty(ask) + "\t" + price(ask) + "\t" + qty(bid) + "\t" + price(bid))
    }
  }

  def run(): Unit = {
    replayEvents()
    out.close()
  }

}
