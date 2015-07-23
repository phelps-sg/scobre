package org.ccfea.tickdata.storage.shuffled.swapper

import org.ccfea.tickdata.event.{OrderEvent, OrderSubmittedEvent, TickDataEvent}
import org.ccfea.tickdata.order.{LimitOrder, OrderWithVolume, TradeDirection}

/**
 * Created by sphelps on 23/07/15.
 */
class OrderSignSwapper
    extends Swapper[Option[TradeDirection.Value]] {

  override def getter(i: Int, ticks: Array[TickDataEvent]) =  {
    ticks(i) match {
      case OrderEvent(_, _, _, OrderWithVolume(_, _, direction, _)) =>
          Some(direction)
      case _ => None
    }
  }

  override def setter(i: Int, x: Option[TradeDirection.Value], ticks: Array[TickDataEvent]) = {
    x match {
      case Some(newTradeDirection) =>
        ticks (i) match {
          case OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode,
                            LimitOrder(orderCode, aggregateSize, tradeDirection, price, trader)) =>
              val revisedOrder =
                new LimitOrder (orderCode, aggregateSize, newTradeDirection, price, trader)
              ticks(i) = new OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, revisedOrder)
          case _ =>
            // Do nothing, TODO check with IMON
        }
      case _ =>
        //no action taken, TODO check with Imon
    }
  }

}
