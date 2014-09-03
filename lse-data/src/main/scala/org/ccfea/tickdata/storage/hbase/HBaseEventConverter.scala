package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.{CellUtil, HBaseConfiguration}
import org.apache.hadoop.hbase.client.{Result, HTable, HBaseAdmin}
import org.apache.hadoop.hbase.util.Bytes
import collection.JavaConversions._
import org.ccfea.tickdata.event.{EventType, Event}
import org.ccfea.tickdata.order.{MarketMechanismType, TradeDirection}
import java.util.Date

/**
 * Misc. functionality for converting events to/from byte arrays.
 * (c) Steve Phelps 2013
 */
trait HBaseEventConverter {

  val conf = HBaseConfiguration.create()
  val admin = new HBaseAdmin(conf)

  /**
   * The table containing the time-series of tick events.
   */
  val eventsTable = new HTable(conf, "events")

  /**
   * A table mapping from trade codes to corresponding order codes.
   */
  val transactionsTable = new HTable(conf, "transactions")

  /**
   * Each table contains a single column-family called 'data'.
   */
  val dataFamily = Bytes.toBytes("data")

  implicit def toBytes(x: Any): Array[Byte] = x match {
    case s: String => Bytes.toBytes(s)
    case evType: EventType.Value => Bytes.toBytes(evType.id)
    case td: TradeDirection.Value => Bytes.toBytes(td.id)
    case mmt: MarketMechanismType.Value => Bytes.toBytes(mmt.id)
    case p: BigDecimal => Bytes.toBytes(new java.math.BigDecimal(p.toString()))
    case l: Long => Bytes.toBytes(l)
  }

  implicit def toEventType(raw: Array[Byte]): EventType.Value = EventType(Bytes.toInt(raw))
  implicit def toTradeDirection(raw: Array[Byte]): TradeDirection.Value = TradeDirection(Bytes.toInt(raw))
  implicit def toMarketMechanismType(raw: Array[Byte]): MarketMechanismType.Value =
                                                                    MarketMechanismType(Bytes.toInt(raw))
  implicit def toString(raw: Array[Byte]): String = Bytes.toString(raw)
  implicit def toLong(raw: Array[Byte]): Long = Bytes.toLong(raw)
  implicit def toBigDecimal(raw: Array[Byte]): BigDecimal = Bytes.toBigDecimal(raw)

  def toOptionAny[A](convert: Array[Byte] => A, raw: Option[Array[Byte]]): Option[A] =
    raw match {
      case Some(bytes) => Some(convert(bytes))
      case None => None
    }
  implicit def toOptionString(raw: Option[Array[Byte]]) =                 toOptionAny(toString, raw)
  implicit def toOptionLong(raw: Option[Array[Byte]]) =                   toOptionAny(toLong, raw)
  implicit def toOptionBigDecimal(raw: Option[Array[Byte]]) =             toOptionAny(toBigDecimal, raw)
  implicit def toOptionTradeDirection(raw: Option[Array[Byte]]) =         toOptionAny(toTradeDirection, raw)
  implicit def toOptionMarketMechanismType(raw: Option[Array[Byte]]) =    toOptionAny(toMarketMechanismType, raw)

  /**
   * Convert a row from HBase into an Event object.
   * @param r  A Result object representing an event.
   * @return  An Event object representing the event.
   */
  implicit def toEvent(r: Result) = {
    new Event(None,
      getColumn(r, "eventType").get,
      getMessageSequenceNumber(r),
      getTimeStamp(r),
      getTiCode(r),
      getColumn(r, "marketSegmentCode").get,
      getColumn(r, "currencyCode").get,
      getColumn(r, "marketMechanismType"),
      getColumn(r, "aggregateSize"),
      getColumn(r, "tradeDirection"),
      getColumn(r, "orderCode"),
      getColumn(r, "tradeSize"),
      getColumn(r, "broadcastUpdateAction"),
      getColumn(r, "marketSectorCode"),
      getColumn(r, "marketMechanismGroup"),
      getColumn(r, "price"),
      getColumn(r, "singleFillInd"),
      getColumn(r, "matchingOrderCode"),
      getColumn(r, "resultingTradeCode"),
      getColumn(r, "tradeCode"),
      getColumn(r, "tradeTimeInd"),
      getColumn(r, "convertedPriceInd")
    )
  }

  /**
   * Get the HBase key for an event.  The key design is:
   *
   *   <tiCode> (12 bytes) | "0" (1 byte) | <timeStamp> (8 bytes) | <messageSequenceNumber> (8 bytes)
   *
   * The "0" separator allows for partial key scans on tiCode (ie a particular asset).
   *
   * @param event  The event to generate a key for
   * @return       An array of bytes representing the HBase key.
   */
  def getKey(event: Event): Array[Byte] = {
    Bytes.add(event.tiCode + "0", event.timeStamp, event.messageSequenceNumber)
  }

  def getMessageSequenceNumber(result: Result): Long = {
    Bytes.toLong(Bytes.tail(result.getRow, 8))
  }

  def getTiCode(result: Result): String = {
    Bytes.head(result.getRow, 12)
  }

  def getColumn(result: Result, name: String): Option[Array[Byte]] = {
    val column =  result.getColumnCells(dataFamily, name)
    column.size match {
      case 0 => None
      case 1 => Some(CellUtil.cloneValue(column(0)))
      case _ => throw new IllegalArgumentException("More than one result in column " + name)
    }
  }

  def getTimeStamp(result: Result): Long = {
    val column = result.getColumnCells(dataFamily, "eventType")
    assert(column.size == 1)
    column(0).getTimestamp
  }

}

