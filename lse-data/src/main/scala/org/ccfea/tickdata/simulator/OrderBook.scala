package org.ccfea.tickdata.simulator


import java.util.PriorityQueue

import net.sourceforge.jasa.market.{AscendingOrderComparator, DescendingOrderComparator, Order, Price}
import org.ccfea.tickdata.util.LazyVar

import scala.collection.{SortedMap, mutable}

class OrderBook {

  val bids = new PriorityQueue[Order](new DescendingOrderComparator())
  val asks = new PriorityQueue[Order](new AscendingOrderComparator())

  def orders(implicit order: Order) = if (order.isBid) bids else asks

  val bidPriceLevels = new PriceLevels()
  val askPriceLevels = new PriceLevels()(Ordering[Price].reverse)

  def levels(implicit order: Order) = if (order.isBid) bidPriceLevels else askPriceLevels

  def size: Int = math.max(askPriceLevels.size, bidPriceLevels.size)

  val signedPriceLevels = new LazyVar[SortedMap[Price, Long]](() =>
    SortedMap[Price, Long]() ++ bidPriceLevels.levels ++ askPriceLevels.negatedVolume)

  def getHighestUnmatchedBid: Order = bids.peek()
  def getLowestUnmatchedAsk: Order = asks.peek()

  def bestBidPrice: Option[Price] = if (bids.isEmpty) None else Some(getHighestUnmatchedBid.getPrice)
  def bestAskPrice: Option[Price] = if (asks.isEmpty) None else Some(getLowestUnmatchedAsk.getPrice)

  def askPrice = askPriceLevels.prices()
  def bidPrice = bidPriceLevels.prices()

  def bidVolume(i: Int) = bidPriceLevels(bidPrice(i))
  def askVolume(i: Int) = askPriceLevels(askPrice(i))

  def incrementLevel(volDelta: Long)(implicit order: Order): Unit = {
    levels.increment(order.getPrice, volDelta)
    signedPriceLevels.unvalidate()
  }

  def add(implicit order: Order): Unit = {
    orders.add(order)
    incrementLevel(order.aggregateUnfilledVolume())
  }

  def remove(implicit order: Order): Unit = {
    if (orders.remove(order)) incrementLevel(-order.getQuantity)
  }

  def removeBest(implicit order: Order): Unit = {
    orders.poll()
    incrementLevel(-order.getQuantity)
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
        incrementLevel(-ba.getQuantity)(bb)
        removeBest(ba)
      } else {
        ba.setQuantity(ba.getQuantity - bb.getQuantity)
        incrementLevel(-bb.getQuantity)(ba)
        removeBest(bb)
      }
    }
    assert(isEmpty || !crossed)
  }

}
