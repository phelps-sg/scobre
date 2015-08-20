package org.ccfea.tickdata.storage.shuffled.swapper

import org.ccfea.tickdata.event.{OrderRevisedEvent, OrderEvent, OrderSubmittedEvent, TickDataEvent}
import org.ccfea.tickdata.order.{LimitOrder, OrderWithVolume, TradeDirection}
import org.ccfea.tickdata.storage.shuffled.RandomPermutation
import grizzled.slf4j.Logger

/**
 * Created by sphelps on 23/07/15.
 */
class OrderSignSwapper
    extends AttributeSwapper[TradeDirection.Value] {

  val logger = Logger(classOf[OrderSignSwapper])

  def getAttribute(order: LimitOrder) = order.tradeDirection

  def getAttribute(event: OrderRevisedEvent) = event.newDirection

  def setAttribute(event: OrderRevisedEvent, x: TradeDirection.Value) = event.copy(newDirection = x)

  def setAttribute(event: OrderSubmittedEvent, x: TradeDirection.Value) = event match {
      case OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode,
                                LimitOrder(orderCode, aggregateSize, tradeDirection, price, trader)) =>
        val revisedOrder =
          new LimitOrder(orderCode, aggregateSize, x, price, trader)
        new OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, revisedOrder)
    }

}
