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

  /**
   * The current state of the book.
   */
  val book = new FourHeapOrderBook()

  /**
   * Lookup table mapping order-codes to Orders.
   */
  val orderMap = collection.mutable.Map[String, Order]()

  /**
   * The time of the most recent event.
   */
  var time: Option[SimulationTime] = None

  /**
   * The most recent transaction price.
   */
  var lastTransactionPrice: Option[Double] = None


  /**
   * Update the state in response to a new incoming event.
   * @param ev  The new event
   */
  def processEvent(ev: Event) = {

    assert(ev.timeStamp >= (time match { case None => 0; case Some(t) => t.getTicks}))

    time = Some(new SimulationTime(ev.timeStamp))

    ev match {

        /********************************************************************
         *        Logic for order submitted events                          *
         ********************************************************************/
      case Event(id, EventType.OrderSubmitted,
                  messageSequenceNumber, timeStamp, tiCode, marketSegmentCode,
                  Some(marketMechanismType), Some(aggregateSize), Some(buySellInd), Some(orderCode),
                  None,
                  Some(broadcastUpdateAction), Some(marketSectorCode), Some(marketMechanismGroup), Some(price),
                    Some(singleFillInd),
                  None, None, None, None, None)

      => {

        val order = new Order()
        order.setPrice(price.toDouble)
        order.setQuantity(aggregateSize.toInt)
        order.setAgent(new SimpleTradingAgent())
        order.setIsBid(buySellInd equals "B")
        order.setTimeStamp(time.get)
        if (orderMap.contains(orderCode)) {
          println("Submission using existing order code" + orderCode)
        }
        orderMap(orderCode) = order
        if (marketMechanismType equals "LO") {
          processLimitOrder(order)
        } else {
          processMarketOrder(order)
        }
      }

        /********************************************************************
         *        Logic for order deleted (and related) events              *
         ********************************************************************/
       case Event(id, EventType.OrderDeleted | EventType.OrderExpired | EventType.TransactionLimit,
                  messageSequenceNumber, timeStamp,
                  tiCode, marketSegmentCode, marketMechanismType, aggregateSize, buySellInd,
                  Some(orderCode),
                  tradeSize, broadcastUpdateAction, marketSectorCode, marketMechanismGroup, price, singleFillInd,
                  None, None, None, None, None)

      => {
        if (orderMap.contains(orderCode)) {
          val order = orderMap(orderCode)
          book.remove(order)
        } else {
          println("Cannot find order for " + orderCode)
        }

      }


        /********************************************************************
         *        Logic for order filled events                             *
         ********************************************************************/
       case Event(id, EventType.OrderFilled,
                  messageSequenceNumber, timeStamp, tiCode,
                  marketSegmentCode, marketMechanismType, aggregateSize, buySellInd,
                  Some(orderCode),
                  tradeSize, broadcastUpdateAction, marketSectorCode, marketMechanismGroup, price, singleFillInd,
                  matchingOrderCode, resultingTradeCode,
                  None, None, None)

      => {
        if (orderMap.contains(orderCode)) {
          val order = orderMap(orderCode)
          book.remove(order)
        } else {
          println("Cannot find order for " + orderCode)
        }
      }

      /********************************************************************
        *        Logic for transaction events                            *
        ********************************************************************/
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

      case _ => println("Do not know how to process " + ev)
    }
  }

  def processLimitOrder(order: Order) = {
    if (order.isAsk) {
      book.insertUnmatchedAsk(order)
    } else {
      book.insertUnmatchedBid(order)
    }
  }

  def processMarketOrder(order: Order) = {
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

  //TODO this results in a sort
  def bidPrice(level: Int) = price(level, book.getUnmatchedBids)
  def askPrice(level: Int) = price(level, book.getUnmatchedAsks)

}
