package org.ccfea.tickdata.simulator

import net.sourceforge.jasa.market.Order
import org.ccfea.tickdata.event._
import org.ccfea.tickdata.order.{Trader, TradeDirection, LimitOrder, MarketOrder}

/**
 * A market-state in which the uncrossing is performed explicitly by the simulator,
 * as opposed to being implied by OrderFilledEvent and OrderMatchedEvent.
 *
 * (C) Steve Phelps 2014
 */
class ClearingMarketState extends MarketState {

  override def postProcessing(ev: TickDataEvent): Unit = {
//    book.uncross()
    super.postProcessing(ev)
    //TODO optionally record most recent transaction price as a result of clearing
  }

  override def checkConsistency(ev: TickDataEvent): Unit = {
  }

  override def insertOrder(order: Order): Unit = {
    book.add(order)
    book.uncross()
  }

  override def process(ev: OrderFilledEvent): Unit = {
    logger.debug("Ignoring OrderFilledEvent with explicit clearing " + ev)
    auctionState = AuctionState.undefined
  }

  override def process(ev: OrderMatchedEvent): Unit = {
    logger.debug("Ignoring OrderMatchedEvent with explicit clearing " + ev)
    auctionState = AuctionState.undefined
  }

  override def processLimitOrder(order: LimitOrder) = {
    super.processLimitOrder(order)
    auctionState = AuctionState.continuous
  }

  override def processMarketOrder(order: MarketOrder) = {
    bestPrice(order) match {
      case Some(price) =>
        val effectiveLimitOrder = new net.sourceforge.jasa.market.Order()
        effectiveLimitOrder.setPrice(price)
        effectiveLimitOrder.setQuantity(order.aggregateSize.intValue())
        effectiveLimitOrder.setIsBid(order.tradeDirection == TradeDirection.Buy)
        effectiveLimitOrder.setTimeStamp(time.get)
        insertOrder(effectiveLimitOrder)
      case None =>
        logger.warn("Ignoring market order because there is no best price: " + order)
        auctionState = AuctionState.undefined
    }
  }

}
