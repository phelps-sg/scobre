package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime

import net.sourceforge.jasa.agent.SimpleTradingAgent

import collection.JavaConversions._

import net.sourceforge.jasa.market.FourHeapOrderBook

import grizzled.slf4j.Logger
import java.util.GregorianCalendar
import org.ccfea.tickdata.event._
import org.ccfea.tickdata.event.OrderFilledEvent
import org.ccfea.tickdata.event.OrderMatchedEvent
import scala.Some
import org.ccfea.tickdata.event.Event
import org.ccfea.tickdata.order.{TradeDirection, AbstractOrder, LimitOrder}
import scala.collection.mutable

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
  val orderMap = collection.mutable.Map[String, net.sourceforge.jasa.market.Order]()

  /**
   *
   * Lookup table mapping each transaction code to a list of the corresponding matched orders
   */
  val transactionMap = collection.mutable.Map[String, mutable.ListBuffer[AbstractOrder]]()

  /**
   * The time of the most recent event.
   */
  var time: Option[SimulationTime] = None

  /**
   * The most recent transaction price.
   */
  var lastTransactionPrice: Option[Double] = None

  var volume: Long = 0

  val logger = Logger(classOf[MarketState])

  def toJasaOrder(o: AbstractOrder): net.sourceforge.jasa.market.Order = {
    val order = new net.sourceforge.jasa.market.Order()
    o match {
      case lo:LimitOrder => {
        order.setPrice(lo.price.toDouble)
        order.setQuantity(lo.aggregateSize.toInt)
        order.setAgent(new SimpleTradingAgent())
        order.setIsBid(lo.tradeDirection == TradeDirection.Buy)
      }
    }
    order.setTimeStamp(time.get)
    order
  }

  /**
   * Update the state in response to a new incoming event.
   * @param ev  The new event
   */
  def newEvent(ev: OrderReplayEvent) = {

    logger.debug("Processing event " + ev)

    assert(ev.timeStamp.getTime >= (time match { case None => 0; case Some(t) => t.getTicks}))

    val newTime = new SimulationTime(ev.timeStamp.getTime)
//
//    this.time match {
//      case Some(t) => if (getDay(newTime) != getDay(t)) startNewDay()
//      case None =>
//    }

    this.time = Some(newTime)

    process(ev)
  }

  def printState = {
    book.printState()
  }

  implicit def price(order: net.sourceforge.jasa.market.Order) = if (order != null) Some(order.getPrice) else None
  def quote = new Quote(book.getHighestUnmatchedBid, book.getLowestUnmatchedAsk)

  def midPrice: Option[Double] = {

    quote match {
      case Quote(None,      None)      => None
      case Quote(Some(bid), None)      => Some(bid)
      case Quote(None,      Some(ask)) => Some(ask)
      case Quote(Some(bid), Some(ask)) => Some((bid + ask) / 2.0)
    }
  }

//  def price(level: Int, orders: Seq[Order]): Option[Double] = {
//    if (level < orders.length) {
//      Some(orders.sorted.get(level).getPrice)
//    } else {
//      None
//    }
//  }

  //TODO this results in a sort
//  def bidPrice(level: Int) = price(level, book.getUnmatchedBids)
//  def askPrice(level: Int) = price(level, book.getUnmatchedAsks)

  def process(ev: OrderReplayEvent): Unit = {
    ev match {
      case tr: TransactionEvent           =>  process(tr)
      case or: OrderRemovedEvent          =>  process(or)
      case of: OrderFilledEvent           =>  process(of)
      case om: OrderMatchedEvent          =>  process(om)
      case lo: OrderSubmittedEvent        =>  process(lo)
      case _ => logger.warn("Unknown event type: " + ev)
    }
  }

  def process(ev: TransactionEvent): Unit = {
    this.lastTransactionPrice = Some(ev.transactionPrice.toDouble)
    this.volume = ev.tradeSize
    if (transactionMap.contains(ev.tradeCode)) {
      val matchedOrders = transactionMap(ev.tradeCode)
      for(order <- matchedOrders) {
        if (orderMap.contains(order.orderCode)) {
          val jasaOrder = orderMap(order.orderCode)
          logger.debug("Adjusting qty for order based on partial match" + ev)
          jasaOrder.setQuantity(jasaOrder.getQuantity - ev.tradeSize.toInt)
          logger.debug("New order = " + jasaOrder)
          assert(jasaOrder.getQuantity > 0)
        } else {
          logger.warn("Could not find order code " + order.orderCode + " for transaction " + ev)
        }
      }
    } else {
      logger.debug("No outstanding orders corresponding to trade code- order filled? " + ev.tradeCode)
    }
  }

  def process(ev: OrderRemovedEvent): Unit = {
    volume = 0
    val orderCode = ev.order.orderCode
    if (orderMap.contains(orderCode)) {
      val order = orderMap(orderCode)
      book.remove(order)
    } else {
      logger.warn("Unknown order code when removing order: " + orderCode)
    }
  }

  def process(ev: OrderFilledEvent): Unit = {
    volume = 0
    val orderCode = ev.order.orderCode
      if (orderMap.contains(orderCode)) {
        val order = orderMap(orderCode)
        logger.debug("Removing order " + orderCode + " from book: " + order)
        book.remove(order)
      } else {
        logger.warn("Unknown order code when order filled: " + orderCode)
      }
  }

  def process(ev: OrderMatchedEvent): Unit = {
    volume = 0
    val orderCode = ev.order.orderCode
    if (orderMap.contains(orderCode)) {
      val order = orderMap(orderCode)
      logger.debug("partially filled order " + order)
      val matchedOrder =
          if (orderMap.contains(ev.matchingOrder.orderCode))  Some(orderMap(ev.matchingOrder.orderCode)) else None
      if (!transactionMap.contains(ev.resultingTradeCode)) {
        transactionMap(ev.resultingTradeCode) = mutable.ListBuffer[AbstractOrder]()
      }
      transactionMap(ev.resultingTradeCode).append(ev.order)
    }  else {
      logger.warn("unknown order code " + orderCode)
    }
  }

  def process(ev: OrderSubmittedEvent): Unit = {
    volume = 0
    val order = ev.order
    if (orderMap.contains(order.orderCode)) {
      logger.warn("Submission using existing order code: " + order.orderCode)
    }
    order match {
       case lo: LimitOrder => {
         val newOrder = toJasaOrder(order)
         if (newOrder.isAsk) book.insertUnmatchedAsk(newOrder) else book.insertUnmatchedBid(newOrder)
         orderMap(order.orderCode) = newOrder
        //    book.add(order)
       }
       case _ =>
    }
  }

//  def startNewDay() = {
//    book.reset()
//  }

  def getDay(t: SimulationTime) = {
    val cal = new GregorianCalendar()
    cal.setTime(new java.util.Date(t.getTicks))
    cal.get(java.util.Calendar.DAY_OF_MONTH)
  }

}
