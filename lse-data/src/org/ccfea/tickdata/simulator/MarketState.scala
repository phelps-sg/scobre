package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime

import net.sourceforge.jasa.agent.SimpleTradingAgent

import collection.JavaConversions._

import net.sourceforge.jasa.market.FourHeapOrderBook

import grizzled.slf4j.Logger
import java.util.{Date, GregorianCalendar}
import org.ccfea.tickdata.event._
import org.ccfea.tickdata.event.OrderFilledEvent
import org.ccfea.tickdata.event.OrderMatchedEvent
import scala.Some
import org.ccfea.tickdata.event.Event
import org.ccfea.tickdata.order.{TradeDirection, AbstractOrder, LimitOrder}
import scala.collection.mutable

/**
 * The state of the market at a single point in time.  This class contains a mutable representation of the entire
 * state of the market, including the order-book, which is updated as new events arrive via the process() methods.
 * The attributes of this class are typically collated as time-series data.
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

  var startOfData: Boolean = false

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
    this.volume = 0

    process(ev)

    checkConsistency(ev)
  }

  def checkConsistency(ev: OrderReplayEvent) = {
    logger.debug("quote = " + quote)
    if (hour > 8) {
      var consistent = false
      do {
        quote match {
          case Quote(Some(bid), Some(ask)) => {
            if (bid > ask) {
              logger.warn("Artificially clearing book to maintain consistency following event " + ev)
              book.remove(book.getHighestUnmatchedBid)
              book.remove(book.getLowestUnmatchedAsk)
            } else {
              consistent = true
            }
          }
          case _ => consistent = true
        }
      } while (!consistent)
    }
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

      case tr: TransactionEvent           =>  startOfData = false; process(tr)
      case or: OrderRemovedEvent          =>  startOfData = false; process(or)
      case of: OrderFilledEvent           =>  startOfData = false; process(of)

      case lo: OrderSubmittedEvent        =>  process(lo)
      case me: MultipleEvent              =>  process(me)

      case om: OrderMatchedEvent          =>  startOfData = false; process(om)

      case so: StartOfDataMarker          =>  if (!startOfData) {
        logger.debug("Resetting book in response to broadcastUpdateAction=='F' event")
        book.reset()
        startOfData = true
      }

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
          adjustQuantity(order.orderCode, ev)
        } else {
          logger.warn("Could not find order code " + order.orderCode + " for transaction " + ev)
        }
      }
    } else {
      logger.debug("No outstanding orders corresponding to trade code- order filled? " + ev.tradeCode)
    }
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

  def process(ev: MultipleEvent): Unit = {
    ev.events match {
      case Nil => // Do nothing
      case head :: tail => {
        process(head)
        process(new MultipleEvent(tail))
      }
    }
  }

  def adjustQuantity(orderCode: String, ev: TransactionEvent): Unit = {
    val jasaOrder = orderMap(orderCode)
    logger.debug("Adjusting qty for order based on partial match" + ev)
    jasaOrder.setQuantity(jasaOrder.getQuantity - ev.tradeSize.toInt)
    logger.debug("New order = " + jasaOrder)
//    assert(jasaOrder.getQuantity >= 0)
    if (jasaOrder.getQuantity <= 0) {
      logger.warn("Removing order with zero or negative volume from book before full match: " + jasaOrder)
      book.remove(jasaOrder)
//      orderMap.remove(orderCode)
    }
//    if (jasaOrder.getQuantity < 0) {
//      logger.warn("Negative quantity detected- adjusting")
//      jasaOrder.setQuantity(0)
//    }
  }

//  def startNewDay() = {
//    book.reset()
//  }

  def calendar = {
    val cal = new GregorianCalendar()
    cal.setTime(new java.util.Date(time.get.getTicks))
    cal
  }

  def day = {
    calendar.get(java.util.Calendar.DAY_OF_MONTH)
  }

  def hour = {
    calendar.get(java.util.Calendar.HOUR_OF_DAY)
  }

  /**
   * Bean-compatible getter for Java clients.
   *
   * @return  The current state of the order-book.
   */
  def getBook() = book

}
