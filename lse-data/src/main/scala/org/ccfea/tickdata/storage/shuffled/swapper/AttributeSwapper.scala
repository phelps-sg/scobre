package org.ccfea.tickdata.storage.shuffled.swapper

import org.ccfea.tickdata.event.{OrderEvent, OrderRemovedEvent, OrderSubmittedEvent, OrderRevisedEvent}
import org.ccfea.tickdata.order.{LimitOrder, Order}
import org.ccfea.tickdata.storage.shuffled.RandomPermutation
import grizzled.slf4j.Logger

/**
 * Created by sphelps on 20/08/15.
 */
trait AttributeSwapper[T] extends Swapper[Option[T]] {

  def logger: Logger

  override def getter(i: Int, ticks: RandomPermutation): Option[T] = {
    ticks(i) match {
      case OrderEvent(_, _, _, lo: LimitOrder) => Some(getAttribute(lo))
      case OrderRemovedEvent(_, _, _, Order(orderCode)) =>
        ticks(orderCode) match {
          case Some(OrderSubmittedEvent(_, _, _, lo: LimitOrder)) => Some(getAttribute(lo))
          case _ => None
        }
      case ore: OrderRevisedEvent => Some(getAttribute(ore))
      case _ => None
    }
  }

  override def setter(i: Int, x: Option[T], ticks: RandomPermutation) {
    x match {
      case Some(value) =>
        ticks(i) match {
          case ose: OrderSubmittedEvent =>
            ticks(i) = setAttribute(ose, value)
          case ore: OrderRevisedEvent =>
            ticks(i) = setAttribute(ore, value)
          case OrderRemovedEvent(_, _, _, Order(orderCode)) =>
            ticks(orderCode) match {
              case Some(ose: OrderSubmittedEvent) =>
                ticks(orderCode) = setAttribute(ose, value)
              case _ =>
                logger.warn("Unknown order code when swapping prices: " + orderCode)
            }
        }
      case None => // Do nothing
    }
  }

  def setAttribute(event: OrderRevisedEvent, x: T): OrderRevisedEvent

  def setAttribute(event: OrderSubmittedEvent, x: T): OrderSubmittedEvent

  def getAttribute(order: LimitOrder): T

  def getAttribute(event: OrderRevisedEvent): T

}
