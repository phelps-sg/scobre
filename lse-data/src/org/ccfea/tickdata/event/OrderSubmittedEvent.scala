package org.ccfea.tickdata.event

import org.ccfea.tickdata.TradeDirection

/**
 * (C) Steve Phelps 2013
 */
abstract class OrderSubmittedEvent extends OrderCodeEvent {
  def aggregateSize: Long
  def tradeDirection: TradeDirection.Value
}

