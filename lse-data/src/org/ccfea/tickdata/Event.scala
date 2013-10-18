package org.ccfea.tickdata

/**
 * An event that has occurred in the exchange.  A time-ordered sequence of Events can be replayed
 * through a simulator in * order to reconstruct the state of the market at any given time.
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
                 //                 currencyCode: String,

                 marketMechanismType: Option[String],
                 aggregateSize: Option[Long],
                 buySellInd: Option[String],
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
)

