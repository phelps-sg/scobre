package org.ccfea.tickdata.storage.shuffled

import org.ccfea.tickdata.event.{OrderSubmittedEvent, OrderEvent, TickDataEvent}
import org.ccfea.tickdata.order.{LimitOrder, OrderWithVolume}

/**
 * Created by sphelps on 21/07/15.
 */
class RandomPermutationOfVolume(source: Seq[TickDataEvent], proportion: Double, windowSize: Int = 1)
    extends RandomPermutation[Long](source, proportion, windowSize,

  getter = (i, ticks) => {
    ticks(i) match {
      case tick: OrderEvent =>
       tick.order  match {
         case order: OrderWithVolume =>
           order.aggregateSize
       }
    }
  },

  setter = (i, newVolume, ticks) => {
    ticks(i) match {
      case originalTick: OrderSubmittedEvent =>
        originalTick.order match {
          case originalOrder: LimitOrder =>
            val revisedOrder =
              new LimitOrder(originalOrder.orderCode, newVolume,
                                originalOrder.tradeDirection, originalOrder.price, originalOrder.trader)
            ticks(i) = new OrderSubmittedEvent(originalTick.timeStamp, originalTick.messageSequenceNumber,
                                                originalTick.tiCode, revisedOrder)
        }
    }
  }

)
