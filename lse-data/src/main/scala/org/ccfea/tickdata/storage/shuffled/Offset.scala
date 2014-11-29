package org.ccfea.tickdata.storage.shuffled

import org.ccfea.tickdata.event.{OrderSubmittedEvent, TickDataEvent}
import org.ccfea.tickdata.order.{AbstractOrder, OffsetOrder}

/**
 * (C) Steve Phelps 2014
 */
class Offset(val offsetOrder: AbstractOrder => OffsetOrder, val source: Iterable[TickDataEvent])
    extends Iterable[TickDataEvent] {

  def convert(event: TickDataEvent): TickDataEvent = {
     event match {
        case ose: OrderSubmittedEvent =>
          new OrderSubmittedEvent(ose.timeStamp, ose.messageSequenceNumber, ose.tiCode, offsetOrder(ose.order))
        case other =>
          other
      }
  }

  override def iterator: Iterator[TickDataEvent] = new Iterator[TickDataEvent] {
    def next(): TickDataEvent =  convert(source.iterator.next())
    def hasNext = source.iterator.hasNext
  }

//  override def length: Int = source.length
//
//  override def apply(idx: Int): TickDataEvent = convert(source.apply(idx))
}
