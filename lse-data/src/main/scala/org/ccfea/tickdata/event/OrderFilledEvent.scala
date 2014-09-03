package org.ccfea.tickdata.event

import java.util.Date
import org.ccfea.tickdata.order.AbstractOrder

/**
 * Order completely filled.  If the corresponding order is on the book then it will be removed after this event.
 *
 * (C) Steve Phelps 2013
 */
case class OrderFilledEvent(val timeStamp: Date, val messageSequenceNumber: Long,
                            val tiCode: String, val order: AbstractOrder, val matchingOrder: AbstractOrder)
  extends OrderEvent

