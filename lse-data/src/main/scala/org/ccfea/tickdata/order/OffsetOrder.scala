package org.ccfea.tickdata.order

import org.ccfea.tickdata.simulator.MarketState

/**
 * A virtual limit-order used for simulation studies in which
 * the price of the order is specified as an offset from the
 * current best price(s).
 *
 * (C) Steve Phelps 2014
 */
abstract class OffsetOrder extends OrderWithVolume {

  def price(implicit marketState: MarketState): BigDecimal =  bestPrice + offset

  def offset: Double

  def bestPrice(implicit marketState: MarketState): Double

  def toLimitOrder(implicit marketState: MarketState) = {
    new LimitOrder(orderCode, aggregateSize, tradeDirection, price)
  }

}
