package org.ccfea.tickdata.simulator

import net.sourceforge.jasa.market.Order
import org.ccfea.tickdata.event.{OrderFilledEvent, OrderMatchedEvent, TickDataEvent}
import org.ccfea.tickdata.order.{TradeDirection, LimitOrder, MarketOrder}

/**
 * A market-state in which the uncrossing is performed explicitly by the simulator,
 * as opposed to being implied by OrderFilledEvent and OrderMatchedEvent.
 *
 * (C) Steve Phelps 2014
 */
class ClearingMarketState extends MarketState {

  override def postProcessing(ev: TickDataEvent): Unit = {
    book.matchOrders()
    //TODO optionally record most recent transaction price as a result of clearing
  }

  override def insertOrder(order: Order): Unit = {
    book.add(order)
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
    val quote: Quote = this.quote()
    val bestPrice = if (order.tradeDirection == TradeDirection.Buy) quote.ask else quote.bid
    bestPrice match {
      case Some(p) =>
        val lo = new LimitOrder(order.orderCode, order.aggregateSize, order.tradeDirection, p)
        processLimitOrder(lo)
      case None =>
        logger.info("Ignoring market order because there is no best price: " + order)
        auctionState = AuctionState.undefined
    }
  }

}
