package org.ccfea.tickdata

/**
 * (C) Steve Phelps 2013
 */
class OrderWithVolume(orderCode: String, val aggregateSize: Long, val tradeDirection: TradeDirection.Value)
  extends Order(orderCode)
