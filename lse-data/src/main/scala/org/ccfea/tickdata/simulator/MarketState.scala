package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import net.sourceforge.jasa.agent.SimpleTradingAgent
import net.sourceforge.jasa.market.{FourHeapOrderBook, Price}
import grizzled.slf4j.Logger
import java.util.GregorianCalendar

import org.ccfea.tickdata.event._
import org.ccfea.tickdata.order.offset.OffsetOrder
import org.ccfea.tickdata.order._
import org.ccfea.tickdata.util.LazyVar

import scala.beans.BeanProperty
import collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.{Publisher, Subscriber}

/**
 * The state of the market at a single point in time.  This class contains a mutable
 * representation of the entire state of the market, including the order-book,
 * which is updated as new events arrive via the process() methods.
 * The attributes of this class are typically collated as time-series data.
 *
 * (c) Steve Phelps 2013
 */
class MarketState extends Subscriber[TickDataEvent, Publisher[TickDataEvent]]
    with Publisher[TickDataEvent] {

  /**
   * The current state of the book.
   */
  @BeanProperty
  val book = new OrderBook()

  /**
   * Lookup table mapping order-codes to Orders.
   */
  val orderMap = mutable.Map[String, net.sourceforge.jasa.market.Order]()

  /**
   * The time of the most recent event.
   */
  var time: Option[SimulationTime] = None

//  /**
//   * The most recent transaction price.
//   */
//  var lastTransactionPrice: Option[Double] = None

//  /**
//   * The current volume.
//   */
//  var volume: Option[Long] = None

  /**
    * The current market quote.
    */
  var quote = new Quote(None, None)

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
    * The most recent event.
    */
  var mostRecentEvent: Option[TickDataEvent] = None

  /**
    * The previous event.
    */
  var previousEvent: Option[TickDataEvent] = None

  /**
   * The most recent uncrossing price.
   */
  var uncrossingPrice: Option[BigDecimal] = None

  var startOfData: Boolean = true

  val logger = Logger(classOf[MarketState])


  override def notify(pub: Publisher[TickDataEvent], event: TickDataEvent): Unit = {
    newEvent(event)
  }

  /**
   * Update the state in response to a new incoming event.
   *
   * @param ev  The next event in the replay sequence.
   */
  def newEvent(ev: TickDataEvent): Unit = {
    preProcessing(ev)
    process(ev)
    postProcessing(ev)
    publish(ev)
  }

  def preProcessing(ev: TickDataEvent): Unit = {
//    assert(ev.timeStamp.getTime >= (time match { case None => 0; case Some(t) => t.getTicks}))
    val newTime = new SimulationTime(ev.timeStamp.getTime)
    this.time = Some(newTime)
//    this.volume = Some(0)
    this.previousEvent = this.mostRecentEvent
    this.mostRecentEvent = Some(ev)
  }

  def postProcessing(ev: TickDataEvent): Unit = {
    stateTransition(ev)
    this.quote = generateQuote()
//    bookChanged()
    checkConsistency(ev)
  }

  /**
   * Process event-specific state changes.
   *
   * @param ev  The subsequent event in the replay sequence.
   */
  def process(ev: TickDataEvent): Unit = {
    logger.debug("Processing " + ev)
    ev match {
      case tr: TransactionEvent           =>  process(tr)
      case or: OrderRemovedEvent          =>  process(or)
      case of: OrderFilledEvent           =>  process(of)
      case lo: OrderSubmittedEvent        =>  process(lo)
      case me: MultipleEvent              =>  process(me)
      case om: OrderMatchedEvent          =>  process(om)
      case or: OrderRevisedEvent          =>  process(or)
      case sd: StartOfDataMarker          =>  process(sd)
      case ne: NoopEvent                  =>  logger.debug("Taking no action for " + ne)
      case _ => logger.error("Unknown event type: " + ev)
    }
  }

  def process(ev: StartOfDataMarker): Unit = {
    logger.info(ev)
    if (this.startOfData) {
//      logger.info("Reseting book on " + ev)
//      this.book.reset()
      startOfData = false
    }
  }

  def process(ev: OrderRevisedEvent): Unit = {
    val orderCode = ev.order.orderCode
    if (orderMap.contains(orderCode)) {
      val order = orderMap(orderCode)
//      // ASX reinsertion rule
//      val reinsert = (math.abs(ev.newPrice.doubleValue() - order.getPrice()) > 10e-4) ||
//                        ev.newVolume.toInt > order.getQuantity()
      val reinsert = true

      if (reinsert) {
        removeOrder(order)
        order.setPrice(ev.newPrice.doubleValue())
        order.setQuantity(ev.newVolume.toInt)
        order.setIsBid(ev.newDirection == TradeDirection.Buy)
        insertOrder(order)
      }
    } else {
      logger.warn("Unknown order code when amending existing order: " + ev.order.orderCode)
      logger.warn("Converting OrderRevisedEvent to OrderSubmittedEvent")
      val newOrder = new LimitOrder(ev.order.orderCode, ev.newVolume, ev.newDirection, ev.newPrice, new Trader())
      process(new OrderSubmittedEvent(ev.timeStamp, ev.messageSequenceNumber, ev.tiCode, newOrder))
    }
  }
//
//  def process(ev: OrderRevisedEvent): Unit = {
//    val orderCode = ev.order.orderCode
//    if (orderMap.contains(orderCode)) {
//      val order = orderMap(orderCode)
//      removeOrder(order)
//      order.setPrice(ev.newPrice.doubleValue())
//      order.setQuantity(ev.newVolume.toInt)
//      order.setIsBid(ev.newDirection == TradeDirection.Buy)
//      insertOrder(order)
//    } else {
//      logger.warn("Unknown order code when amending existing order: " + ev.order.orderCode)
//      logger.warn("Converting OrderRevisedEvent to OrderSubmittedEvent")
//      val newOrder = new LimitOrder(ev.order.orderCode, ev.newVolume, ev.newDirection, ev.newPrice, new Trader())
//      process(new OrderSubmittedEvent(ev.timeStamp, ev.messageSequenceNumber, ev.tiCode, newOrder))
//    }
//  }

  def process(ev: TransactionEvent): Unit = {
//    this.lastTransactionPrice = Some(ev.transactionPrice.toDouble)
//    this.volume = Some(ev.tradeSize)
  }

  def process(ev: OrderRemovedEvent): Unit = {
    val orderCode = ev.order.orderCode
    if (orderMap.contains(orderCode)) {
      val order = orderMap(orderCode)
      logger.debug("Removing from book: " + order)
      removeOrder(order)
    } else {
      logger.warn("Unknown order code when removing order: " + orderCode)
    }
  }

  def process(ev: OrderFilledEvent): Unit = {
    val orderCode = ev.order.orderCode
      if (orderMap.contains(orderCode)) {
        val order = orderMap(orderCode)
        logger.debug("Removing filled order " + orderCode + " from book: " + order)
        removeOrder(order)
      } else {
        logger.debug("Unknown order code when order filled: " + orderCode)
      }
  }

  def process(ev: OrderMatchedEvent): Unit = {
    val orderCode = ev.order.orderCode
    if (orderMap.contains(orderCode)) {
      val order = orderMap(orderCode)
//      adjustQuantity(order, ev)
      logger.debug("partially filled order " + order)
      val matchedOrder =
          if (orderMap.contains(ev.matchingOrder.orderCode))
            Some(orderMap(ev.matchingOrder.orderCode))
          else None
    }  else {
      logger.debug("unknown order code " + orderCode)
    }
  }

  def process(ev: OrderSubmittedEvent): Unit = {
    val order = ev.order
    if (orderMap.contains(order.orderCode)) {
      logger.debug("Submission using existing order code: " + order.orderCode)
      removeOrder(orderMap(order.orderCode))
    }
    order match {
       case lo: LimitOrder =>   processLimitOrder(lo)
       case oo: OffsetOrder =>
         val convertedOrder = oo.toLimitOrder(quote)
         logger.debug("Converted offset order to: " + convertedOrder)
         processLimitOrder(convertedOrder)
       case mo: MarketOrder =>  processMarketOrder(mo)
       case other: Any =>       logger.warn("Ignoring unknown order-type " + other)
    }
  }

  def process(ev: MultipleEvent): Unit = {
    process(ev.events)
  }

  def process(events: Seq[TickDataEvent]): Unit = {
    for (event <- events) process(event)
  }

  def processLimitOrder(order: LimitOrder) = {
    val newOrder = toJasaOrder(order)
    insertOrder(newOrder)
    orderMap(order.orderCode) = newOrder
  }

  def processMarketOrder(order: MarketOrder) = {
    logger.debug("No action required for market order without explicit clearing: " + order)
  }

//  def adjustQuantity(jasaOrder: net.sourceforge.jasa.market.Order,
//                      ev: OrderMatchedEvent): Unit = {
//    logger.debug("Adjusting qty for order based on partial match" + ev)
//    jasaOrder.setQuantity(jasaOrder.getQuantity - ev.tradeSize.toInt)
//    logger.debug("New order = " + jasaOrder)
////    assert(jasaOrder.getQuantity >= 0)
//    if (jasaOrder.getQuantity <= 0) {
//      logger.warn("Removing order with zero or negative volume from book before full match: " + jasaOrder)
//      removeOrder(jasaOrder)
//    }
//  }

  /**
   * Check the consistency of the market after processing the given event,
   * and take any remedial action if the state is inconsistent.
   *
   * @param ev  The event that has just been processed.
   */
  def checkConsistency(ev: TickDataEvent): Unit = {
//    if (this.auctionState == AuctionState.continuous) {
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
//          this.quote = generateQuote()
//        } while (!consistent)
//      }
//    }
  }

  def insertOrder(order: net.sourceforge.jasa.market.Order) = {
//    if (order.isBid) {
//      book.insertUnmatchedBid(order)
//    } else {
//      book.insertUnmatchedAsk(order)
//    }
    book.add(order)
  }

  def printState() = {
//    book.printState()
  }

  implicit def price(order: net.sourceforge.jasa.market.Order) =
    if (order != null) Some(order.getPriceAsDouble) else None

  def generateQuote() = new Quote(book.bestBidPrice, book.bestAskPrice)

  /**
   * When replaying ticks the simulator may inject "virtual" ticks
   * into the replay process.  By default there are no virtual ticks,
   * hence it returns an empty list.
   *
   * @return  A sequence of "virtual" ticks to be injected into the replayer.
   */
  def virtualTicks: Seq[TickDataEvent] = List()


//  implicit def priceToOptionDouble(p: Option[Price]) = p match {
//    case Some(price) => Some(price.doubleValue)
//    case _ => None }

  def midPrice: Option[Price] = quote.midPrice
  def quoteAsk(): Option[Price] = quote.ask
  def quoteBid(): Option[Price] = quote.bid

  def calendar = {
    val cal = new GregorianCalendar()
    cal.setTime(new java.util.Date(time.get.getTicks))
    cal
  }
  def day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
  def hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
  def minute = calendar.get(java.util.Calendar.MINUTE)

  def orderType: Option[Long] = mostRecentEvent match {
    case Some(OrderSubmittedEvent(_, _, _, LimitOrder(_, _, _, _, _))) => Some(0L)
    case Some(OrderSubmittedEvent(_, _, _, MarketOrder(_, _, _, _))) => Some(1L)
    case _ => None
  }

  def orderDirection: Option[Long] = mostRecentEvent match {
    case Some(OrderSubmittedEvent(_, _, _, order:OrderWithVolume)) =>
      if (order.tradeDirection == TradeDirection.Buy) Some(0) else Some(1)
    case _ => None
  }

  def orderPrice: Option[BigDecimal] = mostRecentEvent match {
    case Some(OrderSubmittedEvent(_, _, _, LimitOrder(_, _, _, price, _))) => Some(price)
    case _ => None
  }

  def orderVolume: Option[Long] = mostRecentEvent match {
    case Some(OrderSubmittedEvent(_, _, _, LimitOrder(_, vol, _, _, _))) => Some(vol)
    case Some(OrderSubmittedEvent(_, _, _, MarketOrder(_, vol, _, _))) => Some(vol)
    case _ => None
  }

  def transactionPrice: Option[BigDecimal] = mostRecentEvent match {
    case Some(TransactionEvent(_, _, _, _, transactionPrice, _, _, _)) => Some(transactionPrice)
    case _ => None
  }

  def transactionVolume: Option[Long] = mostRecentEvent match {
    case Some(TransactionEvent(_, _, _, _, _, transactionVolume, _, _)) => Some(transactionVolume)
    case _ => None
  }

  def askDepthTotal = book.asks.asScala.map(_.aggregateUnfilledVolume()).sum

  def bidDepthTotal = book.bids.asScala.map(_.aggregateUnfilledVolume()).sum

  def bestAskDepth =
    if (book.getLowestUnmatchedAsk != null) Some(book.getLowestUnmatchedAsk.aggregateUnfilledVolume()) else None

  def bestBidDepth =
      if (book.getHighestUnmatchedBid != null) Some(book.getHighestUnmatchedBid.aggregateUnfilledVolume()) else None

  def bookSize = book.size

//  val priceLevels = new LazyVar[PriceLevels](() => new PriceLevels(book))
//  def bookChanged() { priceLevels.unvalidate() }

  def bestAskPrice = quote.ask
  def bestBidPrice = quote.bid

  def bestAskVolume: Option[Long] = if (book.asks.size > 0) Some(book.askVolume(0)) else None
  def bestBidVolume: Option[Long] = if (book.bids.size > 0) Some(book.bidVolume(0)) else None

//  /**
//   * Bean-compatible getter for Java clients.
//   *
//   * @return  The current state of the order-book.
//   */
//  def getLastTransactionPrice: Double = {
//    lastTransactionPrice match {
//      case Some(price)  => price
//      case None         => Double.NaN
//    }
//  }

  /**
   * Update the auctionState in response to the new event.
   * This method implements a state-transition function in the form
   * of a simple Finite-State Automata (FSA); that is,
   * the next state depends both on the existing state as well as
   * the subsequent event.
   *
   * @param ev  The next event in the replay sequence.
   */
  def stateTransition(ev: TickDataEvent) {

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
          if (this.hour >= 16 && this.minute >= 30) {
            AuctionState.endOfDay
          } else
            AuctionState.continuous

        case AuctionState.endOfDay =>
          if (this.hour < 8) {
            this.startOfData = true
            AuctionState.startOfDay
          } else
            AuctionState.endOfDay

        // Transition from uncrossing to continuous
        //   i.f.f. we receive a TransactionEvent.
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
   * Remove the specified the order from the book.
   */
  def removeOrder(jasaOrder: net.sourceforge.jasa.market.Order) = {
//    book.removeAll(jasaOrder)
    book.remove(jasaOrder)
  }

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
        order.setPrice(new Price(lo.price.bigDecimal))
        order.setQuantity(lo.aggregateSize.toInt)
        order.setAgent(lo.trader)
        order.setIsBid(lo.tradeDirection == TradeDirection.Buy)
    }
    order.setTimeStamp(time.get)
    order
  }

}
