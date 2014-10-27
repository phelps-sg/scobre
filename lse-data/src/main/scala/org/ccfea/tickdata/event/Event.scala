package org.ccfea.tickdata.event

import grizzled.slf4j.Logger
import java.util.Date
import org.ccfea.tickdata.order._
import org.ccfea.tickdata.order.MarketOrder
import org.ccfea.tickdata.order.LimitOrder
import org.ccfea.tickdata.order.OtherOrder
import scala.Some
import org.ccfea.tickdata.order.Order

/**
 * A non-relational representation of an event that has occurred in the exchange.
 * A time-ordered sequence of Events can be replayed through a simulator in order to
 * reconstruct the state of the market at any given time.  Events are represented as a flat
 * tuple in order to maintain high-performance and avoid complicated joins across many tables.
 * They can be converted to an object-oriented representation using the method
 * toOrderReplayEvent.
 *
 * (c) Steve Phelps 2013
 */
case class Event(eventID: Option[Long],

                 eventType: EventType.Value,

                 messageSequenceNumber: Long,
                 timeStamp: Long,
                 tiCode: String,
                 marketSegmentCode: String,
//                 countryOfRegister: String,
                 currencyCode: String,

                 marketMechanismType: Option[MarketMechanismType.Value],
                 aggregateSize: Option[Long],
                 tradeDirection: Option[TradeDirection.Value],
                 orderCode: Option[String],

                 tradeSize: Option[Long],
                 broadcastUpdateAction: Option[String],

                 marketSectorCode: Option[String],
//                 participantCode: Option[String],
                 marketMechanismGroup: Option[String],
                 price: Option[BigDecimal],
                 singleFillInd: Option[String],

                 //                 orderActionType: Option[String],
                 matchingOrderCode: Option[String],
                 resultingTradeCode: Option[String],

                 tradeCode: Option[String],
                 //                 tradePrice: Option[BigDecimal],
                 //                 tradeTypeInd: Option[String],
                 tradeTimeInd: Option[String],
                 //                 bargainConditions: Option[String],
                 convertedPriceInd: Option[String]
                 //                 publicationTimeStamp: Option[Long]

                  ) {

  val logger = Logger(classOf[Event])

  def toOrderReplayEvent: OrderReplayEvent = {

    this.eventType match {

      case EventType.OrderSubmitted  =>

        val order = marketMechanismType.get match {
            case MarketMechanismType.LimitOrder =>
              new LimitOrder(orderCode.get, aggregateSize.get, tradeDirection.get, price.get)
            case MarketMechanismType.MarketOrder =>
              new MarketOrder(orderCode.get, aggregateSize.get, tradeDirection.get)
            case _ =>
              new OtherOrder(orderCode.get, marketMechanismType.get)
        }
        val date = new Date(timeStamp)
        val orderSubmittedEvent = new OrderSubmittedEvent(date, messageSequenceNumber, tiCode, order)
        if (broadcastUpdateAction == "F") {
          val markerEvent = new StartOfDataMarker(date, messageSequenceNumber, tiCode)
          new MultipleEvent(List(markerEvent, orderSubmittedEvent))
        } else {
          orderSubmittedEvent
        }

      case EventType.OrderDeleted | EventType.OrderExpired | EventType.TransactionLimit  =>
        new OrderRemovedEvent(new Date(timeStamp), messageSequenceNumber, tiCode, new Order(orderCode.get))

      case EventType.OrderFilled =>
        new OrderFilledEvent(new Date(timeStamp), messageSequenceNumber, tiCode, new Order(orderCode.get),
                              new Order(matchingOrderCode.get))

      case EventType.OrderMatched =>
        new OrderMatchedEvent(new Date(timeStamp), messageSequenceNumber, tiCode, new Order(orderCode.get),
                                 new Order(matchingOrderCode.get), resultingTradeCode.get, tradeSize.get)

      case EventType.Transaction =>
        new TransactionEvent(new Date(timeStamp), messageSequenceNumber, tiCode, tradeCode.get, price.get,
                                  tradeSize.get, orderCode, matchingOrderCode)

      case EventType.OrderRevised =>
        new OrderRevisedEvent(new Date(timeStamp), messageSequenceNumber, tiCode, new Order(orderCode.get), price.get,
                                aggregateSize.get)
    }
  }

  implicit def nonRelationalToObjectOriented(x: Event): OrderReplayEvent = x.toOrderReplayEvent
}
