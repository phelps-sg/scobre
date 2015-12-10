package org.ccfea.tickdata.storage.shuffled.copier

import grizzled.slf4j.Logger
import net.sourceforge.jasa.market.Order
import org.ccfea.tickdata.event._
import org.ccfea.tickdata.order.{Order, LimitOrder, OrderWithVolume}
import org.ccfea.tickdata.storage.shuffled.RandomPermutation

/**
  * Created by sphelps on 21/07/15.
  */
class PriceCopier extends AttributeCopier[BigDecimal] {

  val logger = Logger(classOf[PriceCopier])

  def setAttribute(event: OrderRevisedEvent, priceToSet: BigDecimal) = event.copy(newPrice = priceToSet)

  def setAttribute(event: OrderSubmittedEvent, priceToSet: BigDecimal): OrderSubmittedEvent = {
    event match {
      case OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode,
                                LimitOrder(orderCode, aggregateSize, tradeDirection, price, trader)) =>
        val revisedOrder =
          new LimitOrder(orderCode, aggregateSize, tradeDirection, priceToSet, trader)
        new OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, revisedOrder)
    }
  }

  def getAttribute(order: LimitOrder) = order.price

  def getAttribute(event: OrderRevisedEvent) = event.newPrice

}
