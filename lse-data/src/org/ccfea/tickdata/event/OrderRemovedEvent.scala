package org.ccfea.tickdata.event

import org.ccfea.tickdata.AbstractOrder
import java.util.Date

/**
 * (C) Steve Phelps 2013
 */
case class OrderRemovedEvent(val timeStamp: Date, val messageSequenceNumber: Long,
                                val tiCode: String, val order: AbstractOrder) extends OrderEvent

