package org.ccfea.tickdata.order

/**
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
