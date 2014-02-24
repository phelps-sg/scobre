package org.ccfea.tickdata.event

import java.util.Date

/**
 * A trade has occurred.
 *
 * (C) Steve Phelps 2013
 */
case class TransactionEvent(val timeStamp: Date, val messageSequenceNumber: Long,
                            val tiCode: String, val tradeCode: String,
                            val transactionPrice: BigDecimal, val tradeSize: Long,
                             val orderCode: Option[String], val matchingOrderCode: Option[String])
  extends OrderReplayEvent
