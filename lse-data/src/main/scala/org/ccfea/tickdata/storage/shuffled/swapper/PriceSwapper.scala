package org.ccfea.tickdata.storage.shuffled.swapper

import org.ccfea.tickdata.event._
import org.ccfea.tickdata.order.{LimitOrder, OrderWithVolume}
import org.ccfea.tickdata.storage.shuffled.RandomPermutation

/**
  * Created by sphelps on 21/07/15.
  */
class PriceSwapper
  extends Swapper[Option[BigDecimal]] {

     override def getter(i: Int, ticks: RandomPermutation): Option[BigDecimal] = {
       ticks(i) match {
         case OrderEvent(_, _, _, LimitOrder(_, _, _, price, _)) =>
             Some(price)
         case _ => None
       }
     }

     override def setter(i: Int, x: Option[BigDecimal], ticks: RandomPermutation) {
       x match {
         case Some(newPrice) =>
           ticks (i) match {
             case OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode,
                               LimitOrder(orderCode, aggregateSize, tradeDirection, price, trader)) =>
                 val revisedOrder =
                   new LimitOrder (orderCode, aggregateSize, tradeDirection, newPrice, trader)
                 ticks(i) = new OrderSubmittedEvent(timeStamp, messageSequenceNumber, tiCode, revisedOrder)
             case _ =>
               // Do nothing, TODO check with IMON
           }
         case _ =>
           //no action taken, TODO check with Imon
       }
     }

   }
