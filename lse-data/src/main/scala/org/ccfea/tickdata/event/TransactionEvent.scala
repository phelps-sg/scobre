package org.ccfea.tickdata.event

import java.util.Date

/**
 * A trade has occurred.
 *
 * (C) Steve Phelps 2013
 */
case class TransactionEvent(timeStamp: Date, messageSequenceNumber: Long,
                              tiCode: String, tradeCode: String,
                              transactionPrice: BigDecimal, tradeSize: Long,
                              orderCode: Option[String], matchingOrderCode: Option[String])
  extends TickDataEvent
