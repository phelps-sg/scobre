package org.ccfea.tickdata.event

import org.ccfea.tickdata.order.AbstractOrder

/**
 * Abstract super-class of all events pertaining to orders.
 *
 * (C) Steve Phelps 2013
 */
abstract class OrderEvent extends OrderReplayEvent {
  def order: AbstractOrder
}
