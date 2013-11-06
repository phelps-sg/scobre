package org.ccfea.tickdata

/**
 * (C) Steve Phelps 2013
 */
class LimitOrder(orderCode: String, aggregateSize: Long, tradeDirection: TradeDirection.Value,
                 val price: BigDecimal) extends OrderWithVolume(orderCode, aggregateSize, tradeDirection)

