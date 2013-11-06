package org.ccfea.tickdata.event

import org.ccfea.tickdata.{TradeDirection, EventType}
import scala.Some
import grizzled.slf4j.Logger

/**
 * A non-relational representation of an event that has occurred in the exchange.
 * A time-ordered sequence of Events can be replayed through a simulator in order to
 * reconstruct the state of the market at any given time.  Events are represented as a flat
 * tuple in order to maintain high-performance and avoid complicated joins across many tables.
 * They are implicitly converted to an object-oriented representation using the method
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

                 marketMechanismType: Option[String],
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

    this match {

      /********************************************************************
        *        order submitted events                          *
        ********************************************************************/
      case Event(id, EventType.OrderSubmitted,
                  messageSequenceNumber, timeStamp, tiCode, marketSegmentCode, currencyCode,
                  Some(marketMechanismType), Some(aggregateSize), Some(tradeDirection), Some(orderCode),
                  None,
                  Some(broadcastUpdateAction), Some(marketSectorCode), Some(marketMechanismGroup), Some(price),
                  Some(singleFillInd),
                  None, None, None, None, None)

      => {
        marketMechanismType match {
          case "LO" => new
            LimitOrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, orderCode,
                                      aggregateSize, tradeDirection, price)
          case "MO" => new
            MarketOrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, orderCode, aggregateSize, tradeDirection)

          case _ =>
            logger.warn("Unknown marketMechanismType: " + marketMechanismType)
            new OtherOrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, orderCode,
                                            aggregateSize, tradeDirection, marketMechanismType)
        }
      }


      /********************************************************************
        *        Order deleted (and related) events              *
        ********************************************************************/
      case Event(id, EventType.OrderDeleted | EventType.OrderExpired | EventType.TransactionLimit,
                  messageSequenceNumber, timeStamp,
                  tiCode, marketSegmentCode, marketMechanismType, currencyCode, aggregateSize, tradeDirection,
                  Some(orderCode),
                  tradeSize, broadcastUpdateAction, marketSectorCode, marketMechanismGroup, price, singleFillInd,
                  None, None, None, None, None)

      => new OrderRemovedEvent(timeStamp, messageSequenceNumber, tiCode, orderCode)


      /********************************************************************
        *        Logic for order filled events                             *
        ********************************************************************/
      case Event(id, EventType.OrderFilled,
                  messageSequenceNumber, timeStamp, tiCode,
                  marketSegmentCode, currencyCode, marketMechanismType, aggregateSize, tradeDirection,
                  Some(orderCode),
                  tradeSize, broadcastUpdateAction, marketSectorCode, marketMechanismGroup, price, singleFillInd,
                  matchingOrderCode, resultingTradeCode,
                  None, None, None)

      => new OrderFilledEvent(timeStamp, messageSequenceNumber, tiCode, orderCode)

      /********************************************************************
        *        Order matched events
        ********************************************************************/
      case Event(id, EventType.OrderMatched,
                    messageSequenceNumber, timeStamp, tiCode,
                    marketSegmentCode, currencyCode, marketMechanismType, aggregateSize, tradeDirection,
                    Some(orderCode),
                    tradeSize, broadcastUpdateAction, marketSectorCode, marketMechanismGroup, price, singleFillInd,
                    matchingOrderCode, resultingTradeCode,
                    None, None, None)

      => new OrderMatchedEvent(timeStamp, messageSequenceNumber, tiCode, orderCode)

      /********************************************************************
        *        transaction events                            *
        ********************************************************************/
      case Event(id, EventType.Transaction,
                  messageSequenceNumber, timeStamp,
                  tiCode, marketSegmentCode, currencyCode,
                  None, None, None, None,
                  Some(tradeSize), Some(broadcastUpdateAction),
                  None, None, Some(tradePrice), None,
                  None, None,
                  tradeCode, Some(tradeTimeInd), Some(convertedPriceInd))

      => new TransactionEvent(timeStamp, messageSequenceNumber, tiCode, tradePrice, tradeSize)

    }
  }

  implicit def nonRelationalToObjectOriented(x: Event): OrderReplayEvent = x.toOrderReplayEvent
}
