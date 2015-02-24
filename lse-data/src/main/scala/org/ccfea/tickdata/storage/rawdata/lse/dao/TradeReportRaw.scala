package org.ccfea.tickdata.storage.rawdata.lse.dao

import org.ccfea.tickdata.storage.rawdata.lse.LseHasDateTime

/**
 * Object representing transaction records in the original format supplied by LSE.
 * (c) Steve Phelps 2013
 */

case class TradeReportRaw(messageSequenceNumber: Long,
                          tiCode: String,
                          marketSegmentCode: String,
                          countryOfRegister: String,
                          currencyCode: String,
                          tradeCode: Option[String],
                          tradePrice: Option[BigDecimal],
                          tradeSize: Long,
                          date: String,
                          time: String,
                          broadcastUpdateAction: String,
                          tradeTypeInd: String,
                          tradeTimeInd: String,
                          bargainConditions: String,
                          convertedPriceInd: String,
                          publicationDate: String,
                          publicationTime: String) extends LseHasDateTime
