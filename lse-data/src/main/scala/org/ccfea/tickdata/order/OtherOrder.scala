package org.ccfea.tickdata.order

import net.sourceforge.jasa.agent.TradingAgent

/**
 * (C) Steve Phelps 2013
 */
class OtherOrder(val orderCode: String, val marketMechanismType: MarketMechanismType.Value,
                  val trader: TradingAgent) extends AbstractOrder
