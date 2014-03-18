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
import scala.beans.BeanProperty

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
  @BeanProperty
  val book = new FourHeapOrderBook()

  /**
   * Lookup table mapping order-codes to Orders.
   */
  val orderMap = collection.mutable.Map[String, net.sourceforge.jasa.market.Order]()

  /**
   *
   * Lookup table mapping each transaction code to a list of the corresponding matched orders
   */
//  val transactionMap = collection.mutable.Map[String, mutable.ListBuffer[AbstractOrder]]()

  /**
   * The time of the most recent event.
   */
  var time: Option[SimulationTime] = None

  /**
   * The most recent transaction price.
   */
  var lastTransactionPrice: Option[Double] = None

  var volume: Long = 0

  var auctionState = AuctionState.startOfDay

  var mostRecentTransaction: Option[TransactionEvent] = None

  var uncrossingPrice: Option[BigDecimal] = None

//  var startOfData: Boolean = false

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
  def newEvent(ev: OrderReplayEvent): Unit = {

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
  }

  def checkConsistency(ev: OrderReplayEvent): Unit = {
    if (this.auctionState == AuctionState.continuous) {
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

  def process(ev: OrderReplayEvent): Unit = {
    ev match {
      case tr: TransactionEvent           =>  process(tr)
      case or: OrderRemovedEvent          =>  process(or)
      case of: OrderFilledEvent           =>  process(of)
      case lo: OrderSubmittedEvent        =>  process(lo)
      case me: MultipleEvent              =>  process(me)
      case om: OrderMatchedEvent          =>  process(om)
      case _ => logger.warn("Unknown event type: " + ev)
    }
    stateTransition(ev)
    checkConsistency(ev)
  }

  def stateTransition(ev: OrderReplayEvent) {

    val newState: AuctionState.Value =

      auctionState match {

        case AuctionState.startOfDay =>
          ev match {
            case _: OrderSubmittedEvent =>
              AuctionState.batchOpen
            case _ =>
              AuctionState.startOfDay
          }

        case AuctionState.batchOpen =>
          if (this.hour >= 8 && this.minute > 5)
            AuctionState.continuous
          else
            ev match {
              case tr: TransactionEvent =>
                mostRecentTransaction match {
                  case Some(recentTr) =>
                    if (recentTr.timeStamp == tr.timeStamp && recentTr.transactionPrice == tr.transactionPrice) {
                      AuctionState.uncrossing
                    } else {
                      AuctionState.batchOpen
                    }
                  case _ =>
                    mostRecentTransaction = Some(tr)
                    AuctionState.batchOpen
                }
              case _ =>
                AuctionState.batchOpen
            }

        case AuctionState.continuous =>
          if (this.hour >= 16 && this.minute >= 30)
            AuctionState.endOfDay
          else
            AuctionState.continuous

        case AuctionState.endOfDay =>
          if (this.hour < 8)
            AuctionState.startOfDay
          else
            AuctionState.endOfDay

        case AuctionState.uncrossing =>
          ev match {
            case _: TransactionEvent => AuctionState.uncrossing
            case _ => AuctionState.continuous
          }

        case current => current
      }

    logger.debug("newState = " + newState)
    auctionState = newState
  }

  def process(ev: TransactionEvent): Unit = {
    this.lastTransactionPrice = Some(ev.transactionPrice.toDouble)
    this.volume = ev.tradeSize
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
      adjustQuantity(order, ev)
      logger.debug("partially filled order " + order)
      val matchedOrder =
          if (orderMap.contains(ev.matchingOrder.orderCode))  Some(orderMap(ev.matchingOrder.orderCode)) else None
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
       }
       case _ =>
    }
  }

  def process(ev: MultipleEvent): Unit = {
    ev.events match {
      case Nil => // Do nothing
      case (head:StartOfDataMarker) :: tail => {
        logger.warn("Ignoring events tagged with broadCastUpdateAction='F'")
        logger.warn("Ignoring events: " + tail)
      }
      case head :: tail => {
        process(head)
        process(new MultipleEvent(tail))
      }
    }
  }

  def adjustQuantity(jasaOrder: net.sourceforge.jasa.market.Order, ev: OrderMatchedEvent): Unit = {
    logger.debug("Adjusting qty for order based on partial match" + ev)
    jasaOrder.setQuantity(jasaOrder.getQuantity - ev.tradeSize.toInt)
    logger.debug("New order = " + jasaOrder)
//    assert(jasaOrder.getQuantity >= 0)
    if (jasaOrder.getQuantity <= 0) {
      logger.warn("Removing order with zero or negative volume from book before full match: " + jasaOrder)
      book.remove(jasaOrder)
    }
  }

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

  def minute = {
    calendar.get(java.util.Calendar.MINUTE)
  }

  /**
   * Bean-compatible getter for Java clients.
   *
   * @return  The current state of the order-book.
   */
  def getLastTransactionPrice: Double = {
    lastTransactionPrice match {
      case None => 0.0
      case Some(price) => price
    }
  }

}
