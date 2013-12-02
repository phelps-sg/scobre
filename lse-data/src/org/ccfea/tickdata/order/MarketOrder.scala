package org.ccfea.tickdata.order


/**
 * An order for execution at current best price.
 *
 * (C) Steve Phelps 2013
 */
class MarketOrder(val orderCode: String, val aggregateSize: Long, val tradeDirection: TradeDirection.Value)
  extends OrderWithVolume {

  override def toString() = {
    "MarketOrder(" + orderCode + "," + aggregateSize + "," + tradeDirection + ")"
  }

}
