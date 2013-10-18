package org.ccfea.tickdata

import net.sourceforge.jabm.SimulationTime

import net.sourceforge.jasa.market.{Order, FourHeapOrderBook}
import net.sourceforge.jasa.agent.SimpleTradingAgent

import collection.JavaConversions._

/**
 * The state of the market at a single point in time.
 *
 * (c) Steve Phelps 2013
 */
class MarketState {

  val book = new FourHeapOrderBook()
  val orderMap = collection.mutable.Map[String, Order]()
  var time: Option[SimulationTime] = None
  var lastTransactionPrice: Option[Double] = None

  def processEvent(ev: Event) = {

    time = Some(new SimulationTime(ev.timeStamp))

    ev match {

      case Event(id, EventType.OrderSubmitted,
      messageSequenceNumber, timeStamp,
      tiCode, marketSegmentCode,
      Some(marketMechanismType), Some(aggregateSize), Some(buySellInd),
      Some(orderCode),
      None, Some(broadcastUpdateAction), Some(marketSectorCode), Some(marketMechanismGroup), Some(price), Some(singleFillInd),
      None, None, None, None, None)

      => {

        val order = new Order()
        order.setPrice(price.toDouble)
        order.setQuantity(aggregateSize.toInt)
        order.setAgent(new SimpleTradingAgent())
        order.setIsBid(buySellInd equals "B")
        order.setTimeStamp(time.get)
        if (orderMap.contains(orderCode)) {
          //                    println("Order revision to " + orderMap(orderCode))
        }
        orderMap(orderCode) = order
        if (marketMechanismType equals "LO") {
          processLimitOrder(order)
        } else {
          processMarketOrder(order)
        }
      }

      case Event(id, EventType.OrderDeleted | EventType.OrderExpired | EventType.TransactionLimit,
      messageSequenceNumber, timeStamp,
      tiCode, marketSegmentCode,
      marketMechanismType, aggregateSize, buySellInd,
      Some(orderCode),
      tradeSize, broadcastUpdateAction, marketSectorCode, marketMechanismGroup, price, singleFillInd,
      None, None, None, None, None)

      => {
        //                  println("Removing order " + orderCode)
        if (orderMap.contains(orderCode)) {
          val removedOrder = orderMap(orderCode)
          //                    println("Removed " + removedOrder)
          book.remove(removedOrder)
        } else {
          //                    println("Unable to find order " + orderCode)
        }

      }

      case Event(id, EventType.OrderFilled,
      messageSequenceNumber, timeStamp,
      tiCode, marketSegmentCode,
      marketMechanismType, aggregateSize, buySellInd,
      Some(orderCode),
      tradeSize, broadcastUpdateAction, marketSectorCode, marketMechanismGroup, price, singleFillInd,
      matchingOrderCode, resultingTradeCode, None, None, None)

      => {
        //            println("Order matched " + orderCode)
        if (orderMap.contains(orderCode)) {
          val order = orderMap(orderCode)
          //              println("Found order " + order)
          book.remove(order)
        } else {
          //              println("Unable to find order " + orderCode)
        }
      }

      case Event(id, EventType.Transaction,
      messageSequenceNumber, timeStamp,
      tiCode, marketSegmentCode,
      None, None, None, None,
      Some(tradeSize), Some(broadcastUpdateAction),
      None, None, Some(tradePrice), None,
      None, None,
      tradeCode, Some(tradeTimeInd), Some(convertedPriceInd))

      => {
        lastTransactionPrice = Some(tradePrice.toDouble)
      }

      case _ => // println("Do not know how to process " + ev)
    }
  }

  def processLimitOrder(order: Order) = {
    if (order.isAsk) {
      book.insertUnmatchedAsk(order)
    } else {
      book.insertUnmatchedBid(order)
    }
    //    book.matchOrders()
  }

  def processMarketOrder(order: Order) = {
    //    val otherSide =
    //      if (order.isBid) book.getUnmatchedAsks
    //      else book.getUnmatchedBids
    //    var qty = order.getQuantity
    //    for (potentialMatch <- otherSide if qty >= potentialMatch.getQuantity) {
    //      val limitOrderQty = potentialMatch.getQuantity
    //      book.remove(potentialMatch)
    //      qty = qty - limitOrderQty
    //    }
  }

  def printState = {
    book.printState()
  }

  def midPrice: Option[Double] = {
    val bid = book.getHighestUnmatchedBid
    val ask = book.getLowestUnmatchedAsk
    if (bid == null || ask == null) {
      None
    } else {
      Some((bid.getPrice + ask.getPrice) / 2.0)
    }
  }

  def price(level: Int, orders: Seq[Order]): Option[Double] = {
    if (level < orders.length) {
      Some(orders.sorted.get(level).getPrice)
    } else {
      None
    }
  }

  //TODO cache
  def bidPrice(level: Int) = price(level, book.getUnmatchedBids)
  def askPrice(level: Int) = price(level, book.getUnmatchedAsks)

}
