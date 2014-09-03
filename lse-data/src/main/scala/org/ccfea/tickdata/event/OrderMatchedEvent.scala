package org.ccfea.tickdata.event

import java.util.Date
import org.ccfea.tickdata.order.AbstractOrder

/**
 * Order is partially matched with another one.
 *
 * (C) Steve Phelps 2013
 */
case class OrderMatchedEvent(
                              val timeStamp: Date,
                              val messageSequenceNumber: Long,
                              val tiCode: String,
                              val order: AbstractOrder,
                              val matchingOrder: AbstractOrder,
                              val resultingTradeCode: String,
                              val tradeSize: Long
                             )
  extends OrderEvent
