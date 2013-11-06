package org.ccfea.tickdata

import java.util
import scala.collection.mutable
import net.sourceforge.jasa.market.Order

import collection.JavaConversions._

/**
 */
class OrderBook extends net.sourceforge.jasa.market.OrderBook {

  object AscendingOrder extends Ordering[Order] {
    def compare(x: Order, y: Order) = x.compareTo(y)
  }

  val asks = new mutable.TreeSet[Order]()(AscendingOrder.reverse)
  val bids = new mutable.TreeSet[Order]()(AscendingOrder)

  val nullOrder = new Order()
  val emptyList = new util.LinkedList[Order]()

   def add(order: Order) = {
    if (order.isBid) asks += order else bids += order
  }

  def reset() = {}

  def printState() = {}

  def remove(order: Order) = {
    if (order.isBid) bids -= order else asks -= order
  }

  def matchOrders() = emptyList

  def getHighestUnmatchedBid: Order = bids.firstKey

  def getLowestMatchedBid: Order = nullOrder

  def getLowestUnmatchedAsk: Order = asks.firstKey

  def getHighestMatchedAsk: Order = nullOrder

  def askIterator() = asks.iterator

  def bidIterator() = bids.iterator

  def isEmpty: Boolean = asks.isEmpty && bids.isEmpty

  def getDepth: Int = Math.max(asks.size, bids.size)

  def getUnmatchedBids = bids.toList

  def getUnmatchedAsks = asks.toList

  def getQuote(): (Option[Order], Option[Order]) = {
    ( if (bids.isEmpty) None else Some(bids.firstKey), if (asks.isEmpty) None else Some(asks.firstKey))
  }
}
