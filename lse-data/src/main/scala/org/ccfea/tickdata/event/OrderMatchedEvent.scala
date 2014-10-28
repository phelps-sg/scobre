package org.ccfea.tickdata.event

import java.util.Date
import org.ccfea.tickdata.order.AbstractOrder

/**
 * Order is partially matched with another one.
 *
 * (C) Steve Phelps 2013
 */
case class OrderMatchedEvent(
                              timeStamp: Date,
                              messageSequenceNumber: Long,
                              tiCode: String,
                              order: AbstractOrder,
                              matchingOrder: AbstractOrder,
                              resultingTradeCode: String,
                              tradeSize: Long
                             )
  extends OrderEvent
