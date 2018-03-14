package org.ccfea.tickdata.order.offset

import net.sourceforge.jasa.market.Price
import org.ccfea.tickdata.order.{LimitOrder, TradeDirection}
import org.ccfea.tickdata.simulator.Quote

/**
 * (C) Steve Phelps 2014
 */
class OppositeSideOffsetOrder(limitOrder: LimitOrder, initialQuote: Quote)
    extends OffsetOrder(limitOrder, initialQuote) {

  override def bestPrice(quote: Quote): Option[Price] =
    if (this.tradeDirection == TradeDirection.Buy) quote.ask else quote.bid

  override def toString() =
    "OppositeSideOffsetOrder(" + offset + "," + orderCode + "," + originalPrice + "," +
      tradeDirection + "," + aggregateSize + "," + initialQuote + ")"

}
