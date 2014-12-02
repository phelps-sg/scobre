package org.ccfea.tickdata.order

import net.sourceforge.jasa.market.MarketQuote
import org.ccfea.tickdata.simulator.{Quote, MarketState}

/**
 * A virtual limit-order used for simulation studies in which
 * the price of the order is specified as an offset from the
 * current best price(s) at the time the order was placed.
 * This allows the order to be replayed as an offset for
 * random permutation experiments.
 *
 * (C) Steve Phelps 2014
 */
abstract class OffsetOrder(val limitOrder: LimitOrder, val initialQuote: Quote) extends OrderWithVolume {

  val orderCode = limitOrder.orderCode
  val aggregateSize = limitOrder.aggregateSize
  val tradeDirection = limitOrder.tradeDirection
  val originalPrice = limitOrder.price

  val offset: Double = bestPrice(initialQuote) match {
    case Some(best) => (limitOrder.price - best).toDouble
    case None => 0.0
  }

  def price(quote: Quote): BigDecimal =
    bestPrice(quote) match {
      case None => originalPrice
      case Some(p) => p + offset
    }

  def bestPrice(quote: Quote): Option[Double]

  def toLimitOrder(quote: Quote) = {
    new LimitOrder(orderCode, aggregateSize, tradeDirection, price(quote))
  }

}
