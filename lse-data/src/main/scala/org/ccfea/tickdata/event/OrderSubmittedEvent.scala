package org.ccfea.tickdata.event

import java.util.Date
import org.ccfea.tickdata.order.AbstractOrder

/**
 * A new order has been submitted to the exchange.
 *
 * (C) Steve Phelps 2013
 */
case class OrderSubmittedEvent(timeStamp: Date, messageSequenceNumber: Long,
                                  tiCode: String, order: AbstractOrder) extends TickDataEvent
