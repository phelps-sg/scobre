package org.ccfea.tickdata.order.offset

import org.ccfea.tickdata.order.{LimitOrder, TradeDirection}
import org.ccfea.tickdata.simulator.Quote

/**
 * (C) Steve Phelps 2014
 */
class OppositeSideOffsetOrder(limitOrder: LimitOrder, initialQuote: Quote)
    extends OffsetOrder(limitOrder, initialQuote) {

  override def bestPrice(quote: Quote): Option[Double] =
    if (this.tradeDirection == TradeDirection.Buy) quote.ask else quote.bid
}
