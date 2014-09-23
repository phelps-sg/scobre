package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime

import net.sourceforge.jasa.agent.SimpleTradingAgent

import collection.JavaConversions._

import net.sourceforge.jasa.market.FourHeapOrderBook

import grizzled.slf4j.Logger

import java.util.{Observable, GregorianCalendar, Observer}

import org.ccfea.tickdata.event._
import org.ccfea.tickdata.event.OrderFilledEvent
import org.ccfea.tickdata.event.OrderMatchedEvent

import scala.Some

import org.ccfea.tickdata.order.{TradeDirection, AbstractOrder, LimitOrder}

import scala.beans.BeanProperty

/**
 * The state of the market at a single point in time.  This class contains a mutable
 * representation of the entire state of the market, including the order-book,
 * which is updated as new events arrive via the process() methods.
 * The attributes of this class are typically collated as time-series data.
 *
 * (c) Steve Phelps 2013
 */
class MarketState extends Observer {

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
   * Lookup table mapping each transaction code to a
   *  list of the corresponding matched orders.
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

  /**
   * The current volume.
   */
  var volume: Option[Long] = None

  /**
   * The current state of the auction.  Typically market variables
   * such as the mid-price will only be collated when the auction
   * is in continuous mode.
   */
  var auctionState = AuctionState.startOfDay

  /**
   * The most recent transaction event.
   */
  var mostRecentTransaction: Option[TransactionEvent] = None

  /**
   * The most recent uncrossing price.
   */
  var uncrossingPrice: Option[BigDecimal] = None

  val logger = Logger(classOf[MarketState])

  /**
   * Convert an order-replay order into a JASA order.
   *
   * @param o  The order to be converted.
   * @return   An order which can be processed by the JASA classes.
   */
  def toJasaOrder(o: AbstractOrder): net.sourceforge.jasa.market.Order = {
    val order = new net.sourceforge.jasa.market.Order()
    o match {
      case lo:LimitOrder =>
        order.setPrice(lo.price.toDouble)
        order.setQuantity(lo.aggregateSize.toInt)
        order.setAgent(new SimpleTradingAgent())
        order.setIsBid(lo.tradeDirection == TradeDirection.Buy)
    }
    order.setTimeStamp(time.get)
    order
  }

  /**
   * Receive events from any Observable objects the state is listening to.
   * Typically this will be an instance of MarketSimulator.
   *
   * @param o       The sending object.
   * @param arg     The event to be processed.
   */
  @Override
  def update(o: Observable, arg: scala.Any): Unit = {
    arg match {
      case ev: OrderReplayEvent => newEvent(ev)
      case  _ => logger.warn("Unknown event type: " + arg)
    }
  }

  /**
   * Update the state in response to a new incoming event.
   *
   * @param ev  The next event in the replay sequence.
   */
  def newEvent(ev: OrderReplayEvent): Unit = {

//    logger.debug("Processing event " + ev)

    // Processing general to all event types
    assert(ev.timeStamp.getTime >= (time match { case None => 0; case Some(t) => t.getTicks}))
    val newTime = new SimulationTime(ev.timeStamp.getTime)
    this.time = Some(newTime)
    this.volume = Some(0)

    // Event-specific processing
    process(ev)

    // Post-processing and consistency checks
    stateTransition(ev)
    checkConsistency(ev)
  }

  /**
   * Update the auctionState in response to the new event.
   * This method implements a state-transition function in the form
   * of a simple Finite-State Automata (FSA); that is,
   * the next state depends both on the existing state as well as
   * the subsequent event.
   *
   * @param ev  The next event in the replay sequence.
   */
  def stateTransition(ev: OrderReplayEvent) {

    val newState: AuctionState.Value =

      auctionState match {

        // Transition from startOfDay to batchOpen
        //    i.f.f. we recieve an OrderSubmittedEvent
        case AuctionState.startOfDay =>
          ev match {
            case _: OrderSubmittedEvent =>
              AuctionState.batchOpen
            case _ =>
              AuctionState.startOfDay
          }

        // Transition from batchOpen to:
        //    continuous if it is later than 8:05am
        //    uncrossing if a transaction has been received
        case AuctionState.batchOpen =>
          if (this.hour >= 8 && this.minute > 5)
            AuctionState.continuous
          else
            ev match {
              case tr: TransactionEvent =>
                mostRecentTransaction match {
                  case Some(recentTr) =>
                    if (recentTr.timeStamp == tr.timeStamp &&
                          recentTr.transactionPrice == tr.transactionPrice) {
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

        // Transition from continuous to:
        //    endOfDay if it is later than 16:30
        //
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

        // Transition from uncrossing to continuous
        //   i.f.f. we recieve a TransactionEvent.
        case AuctionState.uncrossing =>
          ev match {
            case _: TransactionEvent => AuctionState.uncrossing
            case _ => AuctionState.continuous
          }

        // In all other cases there is no change.
        case current => current
      }

//    logger.debug("newState = " + newState)
    this.auctionState = newState
  }


  /**
   * Process event-specific state changes.
   *
   * @param ev  The subsequent event in the replay sequence.
   */
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
  }

  def process(ev: TransactionEvent): Unit = {
    this.lastTransactionPrice = Some(ev.transactionPrice.toDouble)
    this.volume = Some(ev.tradeSize)
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
          if (orderMap.contains(ev.matchingOrder.orderCode))
            Some(orderMap(ev.matchingOrder.orderCode))
          else None
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
       case lo: LimitOrder =>
         val newOrder = toJasaOrder(order)
         if (newOrder.isAsk)
           book.insertUnmatchedAsk(newOrder)
         else
           book.insertUnmatchedBid(newOrder)
         orderMap(order.orderCode) = newOrder
       case _ =>
    }
  }

  def process(ev: MultipleEvent): Unit = {
    ev.events match {
      case Nil => // Do nothing
      case (head:StartOfDataMarker) :: tail =>
        logger.warn("Ignoring events tagged with broadCastUpdateAction='F'")
        logger.warn("Ignoring events: " + tail)
      case head :: tail =>
        process(head)
        process(new MultipleEvent(tail))
    }
  }

  def adjustQuantity(jasaOrder: net.sourceforge.jasa.market.Order,
                      ev: OrderMatchedEvent): Unit = {
    logger.debug("Adjusting qty for order based on partial match" + ev)
    jasaOrder.setQuantity(jasaOrder.getQuantity - ev.tradeSize.toInt)
    logger.debug("New order = " + jasaOrder)
//    assert(jasaOrder.getQuantity >= 0)
    if (jasaOrder.getQuantity <= 0) {
      logger.warn("Removing order with zero or negative volume from book before full match: " + jasaOrder)
      book.remove(jasaOrder)
    }
  }


  /**
   * Check the consistency of the market after processing the given event,
   * and take any remedial action if the state is inconsistent.
   *
   * @param ev  The event that has just been processed.
   */
  def checkConsistency(ev: OrderReplayEvent): Unit = {
    if (this.auctionState == AuctionState.continuous) {
//      logger.debug("quote = " + quote)
//      if (hour > 8) {
//        var consistent = false
//        do {
//          quote match {
//            case Quote(Some(bid), Some(ask)) =>
//              if (bid > ask) {
//                logger.warn("Artificially clearing book to maintain consistency following event " + ev)
//                book.remove(book.getHighestUnmatchedBid)
//                book.remove(book.getLowestUnmatchedAsk)
//              } else {
//                consistent = true
//              }
//            case _ => consistent = true
//          }
//        } while (!consistent)
//      }
    }
  }

  def printState() = {
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
      case Some(price)  => price
      case None         => Double.NaN
    }
  }

}
