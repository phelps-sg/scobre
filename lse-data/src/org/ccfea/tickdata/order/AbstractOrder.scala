package org.ccfea.tickdata.order

/**
 * Abstract super-class of all order objects.  Equivalence of orders is defined as equivalence of their order codes.
 * (C) Steve Phelps 2013
 */
abstract class AbstractOrder {

  def orderCode: String

  override def equals(other: Any) = {
    other match {
      case o: AbstractOrder => this.orderCode.equals(o.orderCode)
      case _ => false
    }
  }
}
