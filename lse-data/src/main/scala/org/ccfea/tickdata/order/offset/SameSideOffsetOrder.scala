package org.ccfea.tickdata.order.offset

import org.ccfea.tickdata.order.{LimitOrder, TradeDirection}
import org.ccfea.tickdata.simulator.Quote

/**
 * (C) Steve Phelps
 */
class SameSideOffsetOrder(limitOrder: LimitOrder, initialQuote: Quote)
    extends OffsetOrder(limitOrder, initialQuote) {

  def bestPrice(quote: Quote): Option[Double] =
    if (tradeDirection == TradeDirection.Buy) quote.bid else quote.ask

  override def toString() =
    "SameSideOffsetOrder(" + offset + "," + orderCode + "," + originalPrice + "," +
                              tradeDirection + "," + aggregateSize + "," + initialQuote + ")"

}
