package org.ccfea.tickdata.order

import org.ccfea.tickdata.simulator.MarketState

/**
 * A virtual limit-order used for simulation studies in which
 * the price of the order is specified as an offset from the
 * current best price(s) at the time the order was placed.
 * This allows the order to be replayed as an offset for
 * random permutation experiments.
 *
 * (C) Steve Phelps 2014
 */
abstract class OffsetOrder(val limitOrder: LimitOrder, val initialMarketState: MarketState) extends OrderWithVolume {

  val offset: Double = bestPrice(initialMarketState) match {
    case Some(best) => (limitOrder.price - best).toDouble
    case None => 0.0
  }

  val orderCode = limitOrder.orderCode
  val aggregateSize = limitOrder.aggregateSize
  val tradeDirection = limitOrder.tradeDirection
  val originalPrice = limitOrder.price

  def price(implicit marketState: MarketState): BigDecimal =
    bestPrice match {
      case None => originalPrice
      case Some(p) => p + offset
    }

  def bestPrice(implicit marketState: MarketState): Option[Double]

  def toLimitOrder(implicit marketState: MarketState) = {
    new LimitOrder(orderCode, aggregateSize, tradeDirection, price)
  }

}
