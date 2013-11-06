package org.ccfea.tickdata.event

import org.ccfea.tickdata.Order

/**
 * (C) Steve Phelps 2013
 */
case class OrderSubmittedEvent(val timeStamp: Long, val messageSequenceNumber: Long,
                          val tiCode: String, val order: Order) extends OrderReplayEvent
