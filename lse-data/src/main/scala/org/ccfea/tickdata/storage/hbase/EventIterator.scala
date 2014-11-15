package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.client.{Result, ResultScanner, Get}
import collection.JavaConversions._
import org.ccfea.tickdata.event.{OrderMatchedEvent, TickDataEvent, Event, EventType}

/**
 * This class can be used to replay orders from an HBase database.
 * It is an adaptor allowing an HBase ResultScanner to be used in place of an Iterator.
 * Each iteration will result in the next event in the scan being converted to
 * an OrderReplayEvent.
 *
 * @param scanner
 */
class EventIterator(val scanner: ResultScanner) extends Iterator[TickDataEvent] with HBaseEventConverter {

  val resultIterator: Iterator[Result] = scanner.iterator()

  def hasNext: Boolean = {
    val result = resultIterator.hasNext
//    if (!result) scanner.close
    result
  }

  def next(): TickDataEvent = {
    val rawEvent: Event = resultIterator.next()
//    val event: Event =
      // For transaction events we require additional information from the transactions
      // table.
//      if (rawEvent.eventType == EventType.Transaction) {
//        val (orderCode, matchedOrderCode) = lookupOrderCodes(rawEvent.tradeCode.get)
//        rawEvent.copy( orderCode = orderCode, matchingOrderCode = matchedOrderCode)
//      } else {
//        rawEvent
//      }
    rawEvent.toOrderReplayEvent
  }

  /**
   * Retrieve the order-codes of the orders that resulted in the transaction
   * specified by the supplied trade code.
   *
   * @param tradeCode  The identifier of the transaction as keyed in the
   *                   trade_reports files.
   * @return  A tuple of optional Strings identifying the orders that resulted
   *            in this trade.
   */
  def lookupOrderCodes(tradeCode: String): (Option[String], Option[String]) = {
    val get = new Get(tradeCode)
    val result: Result = transactionsTable.get(new Get(tradeCode))
    ( getColumn(result, "orderCode"), getColumn(result, "matchingOrderCode") )
  }

}
