package org.ccfea.tickdata.event

import org.ccfea.tickdata.order.{Order, AbstractOrder}
import java.util.Date

/**
 * Abstract super-class of all events pertaining to orders.
 *
 * (C) Steve Phelps 2013
 */
abstract class OrderEvent extends TickDataEvent {
  def order: AbstractOrder

}

object OrderEvent {

  def unapply(event: OrderEvent) = Some(event.timeStamp, event.messageSequenceNumber, event.tiCode, event.order)

}