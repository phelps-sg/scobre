package org.ccfea.tickdata.order

import net.sourceforge.jasa.agent.TradingAgent


/**
 * An order for execution at current best price.
 *
 * (C) Steve Phelps 2013
 */
class MarketOrder(val orderCode: String, val aggregateSize: Long, val tradeDirection: TradeDirection.Value, val trader: TradingAgent)
  extends OrderWithVolume {

  override def toString() = {
    "MarketOrder(" + orderCode + "," + aggregateSize + "," + tradeDirection + ")"
  }

}
