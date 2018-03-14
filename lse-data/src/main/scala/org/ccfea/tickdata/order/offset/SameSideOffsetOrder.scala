package org.ccfea.tickdata.order.offset

import net.sourceforge.jasa.market.Price
import org.ccfea.tickdata.order.{LimitOrder, TradeDirection}
import org.ccfea.tickdata.simulator.Quote

/**
 * (C) Steve Phelps
 */
class SameSideOffsetOrder(limitOrder: LimitOrder, initialQuote: Quote)
    extends OffsetOrder(limitOrder, initialQuote) {

  def bestPrice(quote: Quote): Option[Price] =
    if (tradeDirection == TradeDirection.Buy) quote.bid else quote.ask

  override def toString() =
    "SameSideOffsetOrder(" + offset + "," + orderCode + "," + originalPrice + "," +
                              tradeDirection + "," + aggregateSize + "," + initialQuote + ")"

}
