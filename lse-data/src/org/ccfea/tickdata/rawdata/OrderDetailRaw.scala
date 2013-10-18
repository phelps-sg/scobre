package org.ccfea.tickdata.rawdata

import org.ccfea.tickdata.HasDateTime

/**
 * Class representing data records in original format as supplied by LSE.
 *
 * (c) Steve Phelps 2013
 */

case class OrderDetailRaw(orderCode: String, marketSegmentCode: String,
                          marketSectorCode: String, tiCode: String,
                          countryofRegister: String, currencyCode: String,
                          participantCode: Option[String], buySellInd: String,
                          marketMechanismGroup: String,
                          marketMechanismType: String,
                          price: BigDecimal, aggregateSize: Long,
                          singleFillInd: String,
                          broadcastUpdateAction: String,
                          date: String,
                          time: String,
                          messageSequenceNumber: Long) extends HasDateTime
