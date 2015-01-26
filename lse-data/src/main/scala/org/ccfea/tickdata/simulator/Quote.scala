package org.ccfea.tickdata.simulator

import net.sourceforge.jasa.agent.strategy.TradeDirectionPolicy
import org.ccfea.tickdata.order.TradeDirection

/**
 * A quote in the market.
 *
 * (C) Steve Phelps 2013
 */
case class Quote(val bid: Option[Double], val ask: Option[Double]) {

    def midPrice = this match {
      case Quote(None,      None)      => None
      case Quote(Some(bid), None)      => Some(bid)
      case Quote(None,      Some(ask)) => Some(ask)
      case Quote(Some(bid), Some(ask)) => Some((bid + ask) / 2.0)
    }

}
