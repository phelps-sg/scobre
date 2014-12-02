package org.ccfea.tickdata.order.offset

import org.ccfea.tickdata.order.LimitOrder
import org.ccfea.tickdata.simulator.Quote

/**
 * (C) Steve Phelps 2014
 */
class MidPriceOffsetOrder(limitOrder: LimitOrder, initialQuote: Quote) extends OffsetOrder(limitOrder, initialQuote) {

  override def bestPrice(quote: Quote): Option[Double] = quote.midPrice

}
