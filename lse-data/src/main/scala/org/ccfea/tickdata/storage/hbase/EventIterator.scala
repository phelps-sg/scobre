package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.client.{Result, ResultScanner}
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.storage.dao.Event

import scala.collection.JavaConverters._

/**
 * This class can be used to replay orders from an HBase database.
 * It is an adaptor allowing an HBase ResultScanner to be used in place of an Iterator.
 * Each iteration will result in the next event in the scan being converted to
 * a TickDataEvent.
 *
 * @param scanner
 */
class EventIterator(val scanner: ResultScanner) extends Iterator[TickDataEvent] with HBaseEventConverter {

  val resultIterator: Iterator[Result] = scanner.iterator().asScala

  def hasNext: Boolean = {
    val result = resultIterator.hasNext
//    if (!result) scanner.close
    result
  }

  def next(): TickDataEvent = {
    val rawEvent: Event = resultIterator.next()
    rawEvent.tick
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
//  def lookupOrderCodes(tradeCode: String): (Option[String], Option[String]) = {
//    val get = new Get(tradeCode)
//    val result: Result = transactionsTable.get(new Get(tradeCode))
//    ( getColumn(result, "orderCode"), getColumn(result, "matchingOrderCode") )
//  }

}
