package org.ccfea.tickdata.order

import org.ccfea.tickdata.simulator.{Quote, MarketState}

/**
 * (C) Steve Phelps
 */
class SameSideOffsetOrder(limitOrder: LimitOrder, initialQuote: Quote)
    extends OffsetOrder(limitOrder, initialQuote) {

  def bestPrice(quote: Quote): Option[Double] = {
    if (tradeDirection == TradeDirection.Buy) quote.bid else quote.ask
  }

  override def toString() =
    "SameSideOffsetOrder(" + offset + "," + orderCode + "," + originalPrice + "," + tradeDirection + "," + aggregateSize + "," + initialQuote.bid + "," + initialQuote.ask + ")"

}
