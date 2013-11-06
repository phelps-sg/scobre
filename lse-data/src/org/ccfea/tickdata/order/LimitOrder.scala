package org.ccfea.tickdata.order


/**
 * (C) Steve Phelps 2013
 */
case class LimitOrder(orderCode: String, aggregateSize: Long, tradeDirection: TradeDirection.Value,
                 price: BigDecimal) extends OrderWithVolume

