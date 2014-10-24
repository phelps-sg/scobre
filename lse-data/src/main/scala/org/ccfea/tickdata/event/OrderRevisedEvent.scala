package org.ccfea.tickdata.event

import java.util.Date

import org.ccfea.tickdata.order.AbstractOrder

/**
 * (C) Steve Phelps 2014
 */
case class OrderRevisedEvent(val timeStamp: Date, val messageSequenceNumber: Long,
                                  val tiCode: String, val order: AbstractOrder) extends OrderEvent

