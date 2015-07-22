package org.ccfea.tickdata.order

import net.sourceforge.jasa.agent.TradingAgent

/**
 * A limit order with corresponding limit price.
 *
 * (C) Steve Phelps 2013
 */
class LimitOrder(val orderCode: String, val aggregateSize: Long, val tradeDirection: TradeDirection.Value,
                 val price: BigDecimal, val trader: TradingAgent) extends OrderWithVolume {

  override def toString(): String = {
    "LimitOrder(" + orderCode + "," + aggregateSize + "," + tradeDirection + "," + price + ")"
  }

}

object LimitOrder {

  def unapply(l: LimitOrder) = Some(l.orderCode, l.aggregateSize, l.tradeDirection, l.price, l.trader)

}

