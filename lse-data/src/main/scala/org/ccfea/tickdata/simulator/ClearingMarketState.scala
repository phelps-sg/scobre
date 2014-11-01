package org.ccfea.tickdata.simulator

import net.sourceforge.jasa.market.Order
import org.ccfea.tickdata.event.{OrderFilledEvent, OrderMatchedEvent, OrderReplayEvent}

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

  override def process(ev: OrderFilledEvent): Unit = {
    logger.debug("Ignoring OrderFilledEvent with explicit clearing " + ev)
  }

  override def process(ev: OrderMatchedEvent): Unit = {
    logger.debug("Ignoring OrderMatchedEvent with explicit clearing " + ev)
  }
}
