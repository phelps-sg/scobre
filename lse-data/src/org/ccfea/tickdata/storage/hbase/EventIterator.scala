package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.client.{Result, ResultScanner, Get}
import collection.JavaConversions._
import org.ccfea.tickdata.event.{OrderMatchedEvent, OrderReplayEvent, Event, EventType}

class EventIterator(val scanner: ResultScanner) extends Iterator[OrderReplayEvent] with HBaseEventConverter {

  val resultIterator: Iterator[Result] = scanner.iterator()

  def hasNext: Boolean = {
    val result = resultIterator.hasNext
//    if (!result) scanner.close
    result
  }

  def next(): OrderReplayEvent = {
    val rawEvent: Event = resultIterator.next()
    val event: Event =
      if (rawEvent.eventType == EventType.Transaction) {
        val (orderCode, matchedOrderCode) = lookupOrderCodes(rawEvent.tradeCode.get)
        rawEvent.copy( orderCode = orderCode, matchingOrderCode = matchedOrderCode)
      } else {
        rawEvent
      }
    event.toOrderReplayEvent
  }

  def lookupOrderCodes(tradeCode: String): (Option[String], Option[String]) = {
    val get = new Get(tradeCode)
    val result: Result = transactionsTable.get(new Get(tradeCode))
    ( getColumn(result, "orderCode"), getColumn(result, "matchingOrderCode") )
  }

}
