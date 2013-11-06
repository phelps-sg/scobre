package org.ccfea.tickdata.event

import org.ccfea.tickdata.TradeDirection

/**
 * (c) Steve Phelps 2013
 */
case class OtherOrderSubmittedEvent(val timeStamp: Long, val messageSequenceNumber: Long, val tiCode: String,
                                    val orderCode: String, val aggregateSize: Long, val tradeDirection: TradeDirection.Value,
                                    val marketMechanismType: String) extends OrderSubmittedEvent
