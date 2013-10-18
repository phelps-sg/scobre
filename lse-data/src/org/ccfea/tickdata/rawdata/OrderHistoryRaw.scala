package org.ccfea.tickdata.rawdata

import org.ccfea.tickdata.HasDateTime

/**
 * Object representing original order-revision records in original format supplied by LSE.
 *
 * (c) Steve Phelps 2013
 */

case class OrderHistoryRaw(orderCode: String,
                           orderActionType: String,
                           matchingOrderCode: Option[String],
                           tradeSize: Long,
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
                           time: String) extends HasDateTime

