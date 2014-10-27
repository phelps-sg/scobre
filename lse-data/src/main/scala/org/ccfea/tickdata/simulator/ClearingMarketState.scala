package org.ccfea.tickdata.simulator

import net.sourceforge.jasa.market.Order
import org.ccfea.tickdata.event.OrderReplayEvent

/**
 * (C) Steve Phelps 2014
 */
class ClearingMarketState extends MarketState {

  override def postProcessing(ev: OrderReplayEvent): Unit = {
    book.matchOrders()
    auctionState = AuctionState.continuous
  }

  override def insertOrder(order: Order): Unit = {
    book.add(order)
  }
}
