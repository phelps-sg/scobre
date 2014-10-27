package org.ccfea.tickdata.simulator

import org.ccfea.tickdata.event.OrderReplayEvent

/**
 * (C) Steve Phelps 2014
 */
class ClearingMarketState extends MarketState {

  override def postProcessing(ev: OrderReplayEvent): Unit = {
    book.matchOrders()
    auctionState = AuctionState.continuous
  }

}
