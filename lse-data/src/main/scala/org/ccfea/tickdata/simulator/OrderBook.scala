package org.ccfea.tickdata.simulator


import java.util.Comparator

import net.sourceforge.jasa.market.{AscendingOrderComparator, DescendingOrderComparator, Order, Price}

import scala.collection.{SortedMap, mutable}
import scala.collection.parallel.ParMap

class OrderBook {

  def comparatorToOrdering(cmp: Comparator[Order]): Ordering[Order] =
    new Ordering[Order] { def compare(x: Order, y: Order) = cmp.compare(x, y) }

  val bidOrdering = comparatorToOrdering(new DescendingOrderComparator())
  val askOrdering = comparatorToOrdering(new AscendingOrderComparator())

  val bids = new mutable.TreeSet[Order]()(bidOrdering)
  val asks = new mutable.TreeSet[Order]()(askOrdering)
  def orders(implicit order: Order) = if (order.isBid) bids else asks

  val bidPriceLevels = new PriceLevels()
  val askPriceLevels = new PriceLevels()(Ordering[Price].reverse)
  def levels(implicit order: Order) = if (order.isBid) bidPriceLevels else askPriceLevels

  def negatedVolume(levels: mutable.TreeMap[Price, Long]) =
    for ((priceLevel, volume) <- levels) yield (priceLevel, -volume)

  def signedPriceLevels =
    SortedMap[Price, Long]() ++ bidPriceLevels.levels ++ negatedVolume(askPriceLevels.levels)

  def getHighestUnmatchedBid: Order = bids.head
  def getLowestUnmatchedAsk: Order = asks.head

  def bestBidPrice: Option[Price] = if (bids.isEmpty) None else Some(getHighestUnmatchedBid.getPrice)
  def bestAskPrice: Option[Price] = if (asks.isEmpty) None else Some(getLowestUnmatchedAsk.getPrice)

  def askPrice = askPriceLevels.prices
  def bidPrice = bidPriceLevels.prices

  def bidVolume(i: Int) = bidPriceLevels(bidPrice(i))
  def askVolume(i: Int) = askPriceLevels(askPrice(i))

  def add(implicit order: Order): Unit = {
    orders.add(order)
    levels.increment(order.getPrice, order.aggregateUnfilledVolume())
  }

  def remove(implicit order: Order): Unit = {
    orders.remove(order)
    levels.increment(order.getPrice, -order.aggregateUnfilledVolume())
  }


  def uncross(): Unit = {
    //TODO
  }

  def size: Int = math.max(askPriceLevels.size, bidPriceLevels.size)

}
