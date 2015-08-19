package org.ccfea.tickdata.storage.shuffled.swapper

import org.ccfea.tickdata.event._
import org.ccfea.tickdata.order.{LimitOrder, Order, OrderWithVolume}
import org.ccfea.tickdata.storage.shuffled.RandomPermutation

/**
 * Created by sphelps on 21/07/15.
 */
class VolumeSwapper
    extends Swapper[Option[Long]] {

  override def getter(i: Int, ticks: RandomPermutation): Option[Long] = {
    ticks(i) match {
      case OrderEvent(_, _, _, OrderWithVolume(_, volume, _, _)) => Some(volume)
      case OrderRemovedEvent(_, _, _, Order(orderCode)) => None //TODO
      case OrderRevisedEvent(_, _, _, _, _, volume, _) => Some(volume)
      case _ => None
    }
  }

  override def setter(i: Int, x: Option[Long], ticks: RandomPermutation) {
    x match {
      case Some(volumeToSet) =>
        ticks(i) match {
          case OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode,
                            LimitOrder(orderCode, aggregateSize, tradeDirection, price, trader)) =>
            val revisedOrder =
              new LimitOrder (orderCode, volumeToSet, tradeDirection, price, trader)
            ticks(i) = new OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, revisedOrder)
          case ore: OrderRevisedEvent =>
            ticks(i) = ore.copy(newVolume = volumeToSet)
          case OrderRemovedEvent(_, _, _, Order(orderCode)) =>
            // TODO
        }
      case _ =>
        //no action taken
    }
  }

}
