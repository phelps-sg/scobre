package org.ccfea.tickdata.event

import org.ccfea.tickdata.TradeDirection

/**
 * (C) Steve Phelps 2013
 */
case class OrderSubmittedEvent(val timeStamp: Long, val messageSequenceNumber: Long, val tiCode: String,
                                val orderCode: String, val price: BigDecimal, val aggregateSize: Long,
                                val tradeDirection: TradeDirection.Value, val marketMechanismType: String)
  extends OrderCodeEvent
