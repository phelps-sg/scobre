package org.ccfea.tickdata.storage.rawdata.lse

/**
 * Object representing original order-revision records in original format supplied by LSE.
 *
 * (c) Steve Phelps 2013
 */
case class OrderHistoryRaw(orderCode: String,
                           orderActionType: String,
                           matchingOrderCode: Option[String],
                           tradeSize: Option[Long],
                           tradeCode: Option[String],
                           tiCode: String,
                           countryofRegister: String,
                           currencyCode: String,
                           marketSegmentCode: String,
                           aggregateSize: Long,
                           buySellInd: String,
                           marketMechanismType: String,
                           messageSequenceNumber: Long,
                           date: String,
                           time: String) extends LseHasDateTime

