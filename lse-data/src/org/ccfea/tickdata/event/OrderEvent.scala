package org.ccfea.tickdata.event

import org.ccfea.tickdata.AbstractOrder

/**
 * (C) Steve Phelps 2013
 */
abstract class OrderEvent extends OrderReplayEvent {
  def order: AbstractOrder
}
