package org.ccfea.tickdata.event

/**
 * (C) Steve Phelps 2013
 */
abstract class OrderCodeEvent extends OrderReplayEvent {
  def orderCode: String
}
