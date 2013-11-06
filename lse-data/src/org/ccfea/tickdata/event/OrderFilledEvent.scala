package org.ccfea.tickdata.event

import java.util.Date
import org.ccfea.tickdata.order.AbstractOrder

/**
 * (C) Steve Phelps 2013
 */
case class OrderFilledEvent(val timeStamp: Date, val messageSequenceNumber: Long,
                            val tiCode: String, val order: AbstractOrder) extends OrderEvent

