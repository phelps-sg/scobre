package org.ccfea.tickdata.event

/**
 * (C) Steve Phelps 2013
 */
case class TransactionEvent(val timeStamp: Long, val messageSequenceNumber: Long,
                            val tiCode: String, val transactionPrice: BigDecimal, val tradeSize: Long)
  extends OrderReplayEvent
