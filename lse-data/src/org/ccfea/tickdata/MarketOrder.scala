package org.ccfea.tickdata

/**
 * (C) Steve Phelps 2013
 */
class MarketOrder(orderCode: String, aggregateSize: Long, tradeDirection: TradeDirection.Value)
    extends OrderWithVolume(orderCode, aggregateSize, tradeDirection)
