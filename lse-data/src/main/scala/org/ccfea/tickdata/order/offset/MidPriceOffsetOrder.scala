package org.ccfea.tickdata.order.offset

import net.sourceforge.jasa.market.Price
import org.ccfea.tickdata.order.LimitOrder
import org.ccfea.tickdata.simulator.Quote

/**
 * (C) Steve Phelps 2014
 */
class MidPriceOffsetOrder(limitOrder: LimitOrder, initialQuote: Quote) extends OffsetOrder(limitOrder, initialQuote) {

  override def bestPrice(quote: Quote): Option[Price] = quote.midPrice

  override def toString() =
    "MidPriceOffsetOrder(" + offset + "," + orderCode + "," + originalPrice + "," +
      tradeDirection + "," + aggregateSize + "," + initialQuote + ")"

}
