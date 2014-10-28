package org.ccfea.tickdata.event

import java.util.Date
import org.ccfea.tickdata.order.AbstractOrder

/**
 * Order completely filled.  If the corresponding order is on the book then it will be removed after this event.
 *
 * (C) Steve Phelps 2013
 */
case class OrderFilledEvent(timeStamp: Date, messageSequenceNumber: Long,
                              tiCode: String, order: AbstractOrder, matchingOrder: AbstractOrder)
  extends OrderEvent

