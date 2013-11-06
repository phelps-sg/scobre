package org.ccfea.tickdata

import net.sourceforge.jabm.SimulationTime

import net.sourceforge.jasa.agent.SimpleTradingAgent

import collection.JavaConversions._

import net.sourceforge.jasa.market.Order
import net.sourceforge.jasa.market.FourHeapOrderBook

import grizzled.slf4j.Logger
import java.util.GregorianCalendar
import org.ccfea.tickdata.event._
import org.ccfea.tickdata.event.OrderFilledEvent
import org.ccfea.tickdata.event.OrderMatchedEvent
import scala.Some
import org.ccfea.tickdata.event.OrderSubmittedEvent
import org.ccfea.tickdata.event.Event

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
//  val book = new OrderBook()

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

  val logger = Logger(classOf[MarketState])

  /**
   * Update the state in response to a new incoming event.
   * @param ev  The new event
   */
  def newEvent(ev: OrderReplayEvent) = {

    logger.debug("Processing event " + ev)

    assert(ev.timeStamp >= (time match { case None => 0; case Some(t) => t.getTicks}))

    val newTime = new SimulationTime(ev.timeStamp)

    this.time match {
      case Some(t) => if (getDay(newTime) != getDay(t)) startNewDay()
      case None =>
    }

    this.time = Some(newTime)

    process(ev)
  }

  def printState = {
    book.printState()
  }

  def midPrice: Option[Double] = {

    val quote: (Option[Order], Option[Order]) =
      (if (book.getHighestUnmatchedBid == null) None else Some(book.getHighestUnmatchedBid),
       if (book.getHighestMatchedAsk==null)     None else Some(book.getHighestMatchedAsk))

    quote match {
      case (None,      None)      => None
      case (Some(bid), None)      => Some(bid.getPrice)
      case (None,      Some(ask)) => Some(ask.getPrice)
      case (Some(bid), Some(ask)) => Some((bid.getPrice + ask.getPrice) / 2)
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
      case lo: LimitOrderSubmittedEvent   =>  process(lo)
      case mo: MarketOrderSubmittedEvent  =>  process(mo)
      case _ => logger.warn("Unknown event type: " + ev)
    }
  }

  def process(ev: TransactionEvent): Unit = {
    this.lastTransactionPrice = Some(ev.transactionPrice.toDouble)
  }

  def process(ev: OrderRemovedEvent): Unit = {
    if (orderMap.contains(ev.orderCode)) {
      val order = orderMap(ev.orderCode)
      book.remove(order)
    } else {
      logger.warn("Unknown order code when removing order: " + ev.orderCode)
    }
  }

  def process(ev: OrderFilledEvent): Unit = {
      if (orderMap.contains(ev.orderCode)) {
        val order = orderMap(ev.orderCode)
        logger.debug("Removing order " + ev.orderCode + " from book: " + order)
        book.remove(order)
      } else {
        logger.warn("Unknown order code when order filled: " + ev.orderCode)
      }
  }

  def process(ev: OrderMatchedEvent): Unit = {
    if (orderMap.contains(ev.orderCode)) {
      val order = orderMap(ev.orderCode)
      logger.debug("partially filled order " + order)
    }  else {
      logger.debug("unknown order code " + ev.orderCode)
    }
  }

  def process(ev: LimitOrderSubmittedEvent): Unit = {
    val order = new Order()
    order.setPrice(ev.price.toDouble)
    order.setQuantity(ev.aggregateSize.toInt)
    order.setAgent(new SimpleTradingAgent())
    order.setIsBid(ev.tradeDirection == TradeDirection.Buy)
    order.setTimeStamp(time.get)
    if (orderMap.contains(ev.orderCode)) {
      logger.warn("Submission using existing order code: " + ev.orderCode)
    }
    orderMap(ev.orderCode) = order
    if (order.isAsk) book.insertUnmatchedAsk(order) else book.insertUnmatchedBid(order)
//    book.add(order)
  }

  def process(ev: MarketOrderSubmittedEvent): Unit = {
    logger.debug("New market order submitted: " + ev)
    // No action required
  }

  def startNewDay() = {
    book.reset()
  }

  def getDay(t: SimulationTime) = {
    val cal = new GregorianCalendar()
    cal.setTime(new java.util.Date(t.getTicks))
    cal.get(java.util.Calendar.DAY_OF_MONTH)
  }

}
