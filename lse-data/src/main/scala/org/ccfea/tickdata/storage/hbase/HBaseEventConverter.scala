package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.{Cell, CellUtil}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.ccfea.tickdata.storage.dao.{Event, EventType}

import collection.JavaConversions._
import org.ccfea.tickdata.order.{MarketMechanismType, TradeDirection}
import java.util.Date

/**
 * Misc. functionality for converting events to/from byte arrays.
 * (c) Steve Phelps 2013
 */
trait HBaseEventConverter extends java.io.Serializable {

  val TI_LEN = 12

  /**
   * Each table contains a single column-family called 'data'.
   */
  val dataFamily = Bytes.toBytes("data")


  implicit def toBytes(x: Any): Array[Byte] = x match {
    case s: String => Bytes.toBytes(s)
    case evType: EventType.Value => Bytes.toBytes(evType.id.toShort)
    case td: TradeDirection.Value => Bytes.toBytes(td.id.toShort)
    case mmt: MarketMechanismType.Value => Bytes.toBytes(mmt.id.toShort)
    case p: BigDecimal => Bytes.toBytes(new java.math.BigDecimal(p.toString()))
    case l: Long => Bytes.toBytes(l)
    case i: Int => Bytes.toBytes(i)
  }

  implicit def toEventType(raw: Array[Byte]): EventType.Value = EventType(Bytes.toShort(raw))
  implicit def toTradeDirection(raw: Array[Byte]): TradeDirection.Value = TradeDirection(Bytes.toShort(raw))
  implicit def toMarketMechanismType(raw: Array[Byte]): MarketMechanismType.Value =
                                                                    MarketMechanismType(Bytes.toShort(raw))
  implicit def toString(raw: Array[Byte]): String = Bytes.toString(raw)
  implicit def toLong(raw: Array[Byte]): Long = Bytes.toLong(raw)
  implicit def toBigDecimal(raw: Array[Byte]): BigDecimal = Bytes.toBigDecimal(raw)

  def toOptionAny[A](convert: Array[Byte] => A, raw: Option[Array[Byte]]): Option[A] =
    raw match {
      case Some(bytes) => Some(convert(bytes))
      case None => None
    }
  implicit def toOptionStringB(raw: Option[Array[Byte]]) =                 toOptionAny(toString, raw)
  implicit def toOptionLongB(raw: Option[Array[Byte]]) =                   toOptionAny(toLong, raw)
  implicit def toOptionBigDecimalB(raw: Option[Array[Byte]]) =             toOptionAny(toBigDecimal, raw)
  implicit def toOptionTradeDirectionB(raw: Option[Array[Byte]]) =         toOptionAny(toTradeDirection, raw)
  implicit def toOptionMarketMechanismTypeB(raw: Option[Array[Byte]]) =    toOptionAny(toMarketMechanismType, raw)

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
   * Generate the HBase key for an event.  The key design is:
   *
   *   <tiCode> (12 bytes) | "0" (1 byte) | <timeStamp> (8 bytes) | <messageSequenceNumber> (8 bytes) | <internalMSN> (4 bytes)
   *
   * The "0" separator allows for partial key scans on tiCode (ie a particular asset).  Note
   * that this method will *not* append the salt.
   *
   * @param event  The event to generate a key for
   * @return       An array of bytes representing the HBase key.
   */
  def getKey(event: Event, internalMSN: Int): Array[Byte] = {
    Bytes.add(Bytes.add(pad(event.tiCode) + "0", event.timeStamp, event.messageSequenceNumber), internalMSN)
  }

  def pad(s: String, len: Int = TI_LEN) = {
    var result = s
    while (result.length < len) {
      result = result + " "
    }
    result
  }

  def getMessageSequenceNumber(result: Result): Long = {
    Bytes.toLong(Bytes.copy(result.getRow, 12 + 1 + 8, 8))
  }

  def getTiCode(result: Result): String = {
    Bytes.head(result.getRow, TI_LEN)
  }

  def getColumn(result: Result, name: String): Option[Array[Byte]] = {
    val column =  result.getColumnCells(dataFamily, name)
    column.size match {
      case 0 => None
      case 1 => Some(CellUtil.cloneValue(column.get(0)))
      case _ => throw new IllegalArgumentException("More than one result in column " + name)
    }
  }

  def getTimeStamp(result: Result): Long = {
    val column: java.util.List[Cell] = result.getColumnCells(dataFamily, "eventType")
    assert(column.size == 1)
    val firstColumn: Cell = column.get(0)
    firstColumn.getTimestamp
  }

}

