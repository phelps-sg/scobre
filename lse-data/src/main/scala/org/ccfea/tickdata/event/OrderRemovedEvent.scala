package org.ccfea.tickdata.event

import java.util.Date
import org.ccfea.tickdata.order.AbstractOrder

/**
 * An order has been removed from the exchange.
 *
 * (C) Steve Phelps 2013
 */
case class OrderRemovedEvent(val timeStamp: Date, val messageSequenceNumber: Long,
                                val tiCode: String, val order: AbstractOrder) extends OrderEvent

