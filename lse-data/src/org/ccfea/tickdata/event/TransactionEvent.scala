package org.ccfea.tickdata.event

import java.util.Date

/**
 * (C) Steve Phelps 2013
 */
case class TransactionEvent(val timeStamp: Date, val messageSequenceNumber: Long,
                            val tiCode: String, val tradeCode: String,
                            val transactionPrice: BigDecimal, val tradeSize: Long)
  extends OrderReplayEvent
