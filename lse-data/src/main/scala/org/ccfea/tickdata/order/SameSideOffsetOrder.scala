package org.ccfea.tickdata.order

import org.ccfea.tickdata.simulator.MarketState

/**
 * (C) Steve Phelps
 */
class SameSideOffsetOrder(limitOrder: LimitOrder, initialMarketState: MarketState)
    extends OffsetOrder(limitOrder, initialMarketState) {

  def bestPrice(implicit marketState: MarketState): Option[Double] = {
    val quote = marketState.quote
    if (tradeDirection == TradeDirection.Buy) quote.bid else quote.ask
  }

  override def toString() =
    "SameSideOffsetOrder(" + offset + ")"

}
