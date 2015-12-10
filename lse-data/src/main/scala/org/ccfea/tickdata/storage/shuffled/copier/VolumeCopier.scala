package org.ccfea.tickdata.storage.shuffled.copier

import grizzled.slf4j.Logger
import org.ccfea.tickdata.event._
import org.ccfea.tickdata.order.{LimitOrder, Order, OrderWithVolume}
import org.ccfea.tickdata.storage.shuffled.RandomPermutation

/**
 * Created by sphelps on 21/07/15.
 */
class VolumeCopier
  extends AttributeCopier[Long] {

  val logger = Logger(classOf[VolumeCopier])

  def setAttribute(event: OrderRevisedEvent, volumeToSet: Long) = event.copy(newVolume = volumeToSet)

  def setAttribute(event: OrderSubmittedEvent, volumeToSet: Long): OrderSubmittedEvent = {
    event match {
      case OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode,
                                LimitOrder(orderCode, aggregateSize, tradeDirection, price, trader)) =>
        val revisedOrder =
          new LimitOrder(orderCode, volumeToSet, tradeDirection, price, trader)
        new OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, revisedOrder)
    }
  }

  def getAttribute(order: LimitOrder) = order.aggregateSize

  def getAttribute(event: OrderRevisedEvent) = event.newVolume

}
