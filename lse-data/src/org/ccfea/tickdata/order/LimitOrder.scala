package org.ccfea.tickdata.order


/**
 * (C) Steve Phelps 2013
 */
class LimitOrder(val orderCode: String, val aggregateSize: Long, val tradeDirection: TradeDirection.Value,
                 val price: BigDecimal) extends OrderWithVolume {

  override def toString(): String = {
    "LimitOrder(" + orderCode + "," + aggregateSize + "," + tradeDirection + "," + price + ")"
  }

}

