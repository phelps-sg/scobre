package org.ccfea.tickdata.storage.shuffled.copier

import org.ccfea.tickdata.event.{OrderEvent, OrderRevisedEvent, OrderSubmittedEvent, TickDataEvent}
import org.ccfea.tickdata.order.{LimitOrder, OrderWithVolume, TradeDirection, Trader}
import org.ccfea.tickdata.storage.shuffled.RandomPermutation
import grizzled.slf4j.Logger

/**
 * Created by sphelps on 23/07/15.
 */
class OrderSignCopier
    extends AttributeCopier[TradeDirection.Value] {

  val logger = Logger(classOf[OrderSignCopier])

  def getAttribute(order: LimitOrder) = order.tradeDirection

  def getAttribute(event: OrderRevisedEvent) = event.newDirection

  def setAttribute(event: OrderRevisedEvent, x: TradeDirection.Value) = event.copy(newDirection = x)

  def setAttribute(event: OrderSubmittedEvent, x: TradeDirection.Value) = event match {
      case OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode,
                                LimitOrder(orderCode, aggregateSize, tradeDirection, price, trader)) =>
        val revisedOrder =
          new LimitOrder(orderCode, aggregateSize, x, price, new Trader())
        new OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, revisedOrder)
    }

}
