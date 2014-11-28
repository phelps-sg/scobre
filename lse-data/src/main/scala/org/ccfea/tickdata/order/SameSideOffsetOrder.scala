package org.ccfea.tickdata.order

import org.ccfea.tickdata.simulator.MarketState

/**
 * (C) Steve Phelps
 */
class SameSideOffsetOrder(val offset: Double, val aggregateSize: Long, val tradeDirection: TradeDirection.Value,
                            val orderCode: String) extends OffsetOrder {

  def bestPrice(implicit marketState: MarketState) =
    if (tradeDirection == TradeDirection.Buy)
      marketState.book.getHighestUnmatchedBid.getPrice
    else
      marketState.book.getLowestUnmatchedAsk.getPrice

}
