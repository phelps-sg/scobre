package org.ccfea.tickdata.simulator


import java.util.{Comparator, PriorityQueue}

import net.sourceforge.jasa.market.{AscendingOrderComparator, DescendingOrderComparator, Order, Price}

import scala.collection.{SortedMap, mutable}

class OrderBook {

  val bids = new PriorityQueue[Order](new DescendingOrderComparator())
  val asks = new PriorityQueue[Order](new AscendingOrderComparator())

  def orders(implicit order: Order) = if (order.isBid) bids else asks

  val bidPriceLevels = new PriceLevels()
  val askPriceLevels = new PriceLevels()(Ordering[Price].reverse)

  def levels(implicit order: Order) = if (order.isBid) bidPriceLevels else askPriceLevels

  def size: Int = math.max(askPriceLevels.size, bidPriceLevels.size)

  def negatedVolume(levels: mutable.TreeMap[Price, Long]) =
    for ((priceLevel, volume) <- levels) yield (priceLevel, -volume)

  def signedPriceLevels =
    SortedMap[Price, Long]() ++ bidPriceLevels.levels ++ negatedVolume(askPriceLevels.levels)

  def getHighestUnmatchedBid: Order = bids.peek()
  def getLowestUnmatchedAsk: Order = asks.peek()

  def bestBidPrice: Option[Price] = if (bids.isEmpty) None else Some(getHighestUnmatchedBid.getPrice)
  def bestAskPrice: Option[Price] = if (asks.isEmpty) None else Some(getLowestUnmatchedAsk.getPrice)

  def askPrice = askPriceLevels.prices()
  def bidPrice = bidPriceLevels.prices()

  def bidVolume(i: Int) = bidPriceLevels(bidPrice(i))
  def askVolume(i: Int) = askPriceLevels(askPrice(i))

  def add(implicit order: Order): Unit = {
    orders.add(order)
    levels.increment(order.getPrice, order.aggregateUnfilledVolume())
  }

  def remove(implicit order: Order): Unit = {
    if (orders.remove(order)) levels.increment(order.getPrice, -order.getQuantity)
  }

  def removeBest(implicit order: Order): Unit = {
    orders.poll()
    levels.increment(order.getPrice, -order.getQuantity)
  }

  def crossed: Boolean = bids.peek.getPrice.longValue >= asks.peek.getPrice.longValue

  def isEmpty: Boolean = asks.isEmpty || bids.isEmpty

  def uncross(): Unit = {
    while (!isEmpty && crossed)  {
      val bb = bids.peek()
      val ba = asks.peek()
      if (bb.getQuantity == ba.getQuantity) {
        removeBest(ba)
        removeBest(bb)
      } else if (bb.getQuantity > ba.getQuantity) {
        bb.setQuantity(bb.getQuantity - ba.getQuantity)
        bidPriceLevels.increment(bb.getPrice, -ba.getQuantity)
        removeBest(ba)
      } else {
        ba.setQuantity(ba.getQuantity - bb.getQuantity)
        askPriceLevels.increment(ba.getPrice, -bb.getQuantity)
        removeBest(bb)
      }
    }
    assert(isEmpty || !crossed)
  }

}
