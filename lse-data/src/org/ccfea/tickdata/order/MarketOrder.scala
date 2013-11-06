package org.ccfea.tickdata.order


/**
 * (C) Steve Phelps 2013
 */
case class MarketOrder(orderCode: String, aggregateSize: Long, tradeDirection: TradeDirection.Value) extends OrderWithVolume
