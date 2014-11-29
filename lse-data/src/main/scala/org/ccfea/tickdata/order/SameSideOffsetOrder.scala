package org.ccfea.tickdata.order

import org.ccfea.tickdata.simulator.MarketState

/**
 * (C) Steve Phelps
 */
class SameSideOffsetOrder(limitOrder: LimitOrder, initialMarketState: MarketState)
    extends OffsetOrder(limitOrder, initialMarketState) {

  def bestPrice(implicit marketState: MarketState) = {
    val quote = marketState.quote
    if (tradeDirection == TradeDirection.Buy) SomethingOrZero(quote.bid) else SomethingOrZero(quote.ask)
  }

  def SomethingOrZero(p: Option[Double]): Double =
    //TODO: In this case the _offset_ should be zero?
    p match  {
      case Some(p) => p
      case None => 100.0
    }

  override def toString() =
    "SameSideOffsetOrder(" + offset + ")"

}
