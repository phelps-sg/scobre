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
  val orderMap = collection.mutable.Map[String, net.sourceforge.jasa.market.Order]()

  /**
   * The time of the most recent event.
   */
  var time: Option[SimulationTime] = None

  /**
   * The most recent transaction price.
   */
  var lastTransactionPrice: Option[Double] = None

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

    val quote: (Option[Double], Option[Double]) =
      (if (book.getHighestUnmatchedBid == null) None else Some(book.getHighestUnmatchedBid.getPrice),
       if (book.getHighestMatchedAsk==null)     None else Some(book.getHighestMatchedAsk.getPrice))

    quote match {
      case (None,      None)      => None
      case (Some(bid), None)      => Some(bid)
      case (None,      Some(ask)) => Some(ask)
      case (Some(bid), Some(ask)) => Some((bid + ask) / 2.0)
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
  }

  def process(ev: OrderRemovedEvent): Unit = {
    val orderCode = ev.order.orderCode
    if (orderMap.contains(orderCode)) {
      val order = orderMap(orderCode)
      book.remove(order)
    } else {
      logger.warn("Unknown order code when removing order: " + orderCode)
    }
  }

  def process(ev: OrderFilledEvent): Unit = {
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
    val orderCode = ev.order.orderCode
    if (orderMap.contains(orderCode)) {
      val order = orderMap(orderCode)
      logger.debug("partially filled order " + order)
      val existingQty: Int = order.getQuantity
      val matchedOrder =
          if (orderMap.contains(ev.matchingOrder.orderCode))  Some(orderMap(ev.matchingOrder.orderCode)) else None
      val newQty = matchedOrder match {
        case None           => ev.aggregateSize.toInt
        case Some(matched)  => existingQty - Math.min(order.getQuantity, matched.getQuantity)
      }
      order.setQuantity(newQty)
      if(order.getQuantity >= existingQty || newQty <= 0) {
        logger.warn("Partial match did not result in volume decrease: event=" + ev + " order=" + order)
        logger.warn("matchedOrder = " + matchedOrder)
      }
      if (newQty <= 0) {
        logger.error("Negative volume detected: removing order from book to correct")
        book.remove(order)
      }
    }  else {
      logger.warn("unknown order code " + orderCode)
    }
  }

  def process(ev: OrderSubmittedEvent): Unit = {
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

  def startNewDay() = {
    book.reset()
  }

  def getDay(t: SimulationTime) = {
    val cal = new GregorianCalendar()
    cal.setTime(new java.util.Date(t.getTicks))
    cal.get(java.util.Calendar.DAY_OF_MONTH)
  }

}
