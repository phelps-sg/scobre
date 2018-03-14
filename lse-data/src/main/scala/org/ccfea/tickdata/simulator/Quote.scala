package org.ccfea.tickdata.simulator

import net.sourceforge.jasa.agent.strategy.TradeDirectionPolicy
import net.sourceforge.jasa.market.Price
import org.ccfea.tickdata.order.TradeDirection

/**
 * A quote in the market.
 *
 * (C) Steve Phelps 2013
 */
case class Quote(val bid: Option[Price], val ask: Option[Price]) {

    def midPrice = this match {
      case Quote(None,      None)      => None
      case Quote(Some(bid), None)      => Some(bid)
      case Quote(None,      Some(ask)) => Some(ask)
      case Quote(Some(bid), Some(ask)) => Some(new Price((bid.doubleValue() + ask.doubleValue()) / 2.0))
    }

}
