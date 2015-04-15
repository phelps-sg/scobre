package org.ccfea.tickdata.storage.shuffled

import grizzled.slf4j.Logger
import org.ccfea.tickdata.event.{OrderRevisedEvent, OrderSubmittedEvent, TickDataEvent}
import org.ccfea.tickdata.order.offset.OffsetOrder
import org.ccfea.tickdata.order.{LimitOrder, AbstractOrder}
import org.ccfea.tickdata.simulator.{Quote, MarketState}

/**
 * (C) Steve Phelps 2014
 */
class OffsettedTicks(val marketState: MarketState, val ticks: Iterable[TickDataEvent],
              val createOffsetOrder: (LimitOrder,Quote) => OffsetOrder)
    extends Iterable[TickDataEvent] {

  val logger = Logger("org.ccfea.tickdata.OrderReplayer")

  val offsetTicks = for(tick <- ticks) yield convertToOffset(tick)

  def convertToOffset(tick: TickDataEvent) = {
    val convertedTick = tick match {
      case _: OrderSubmittedEvent | _: OrderRevisedEvent =>
        val limitOrder = tick match {
          case os: OrderSubmittedEvent =>
            os.order match {case lo: LimitOrder => lo}
          case or: OrderRevisedEvent =>
            // Convert the OrderRevisedEvent into an OrderSubmittedEvent
            // by first extracting the implied order
            // TODO:  Ensure that the trader id is the same in the new order
            new LimitOrder(or.order.orderCode, or.newVolume, or.newDirection, or.newPrice)
        }
        val offsetOrder = createOffsetOrder(limitOrder, marketState.quote())
        new OrderSubmittedEvent(tick.timeStamp, tick.messageSequenceNumber, tick.tiCode, offsetOrder)
      case other =>
        tick
    }
    marketState.newEvent(tick)
    logger.debug("Converted " + tick + " to " + convertedTick)
    convertedTick
  }

  override def iterator: Iterator[TickDataEvent] = offsetTicks.iterator
}
