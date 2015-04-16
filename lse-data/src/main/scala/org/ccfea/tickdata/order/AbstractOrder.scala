package org.ccfea.tickdata.order

import net.sourceforge.jasa.agent.TradingAgent

/**
 * Abstract super-class of all order objects.  Equivalence of orders is defined as equivalence of their order codes.
 * (C) Steve Phelps 2013
 */
abstract class AbstractOrder {

  def orderCode: String
  def trader: TradingAgent

  override def equals(other: Any) = {
    other match {
      case o: AbstractOrder => this.orderCode.equals(o.orderCode)
      case _ => false
    }
  }
}
