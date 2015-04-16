package org.ccfea.tickdata.order

import net.sourceforge.jasa.agent.TradingAgent

/**
 * (C) Steve Phelps 2013
 */
case class Order(val orderCode: String) extends AbstractOrder {

  val trader = new Trader()

}

