package org.ccfea.tickdata.hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{HTable, HBaseAdmin}
import org.apache.hadoop.hbase.util.Bytes
import org.ccfea.tickdata.{Event, EventType}

/**
 * Misc. functionality for converting events to/from byte arrays.
 * (c) Steve Phelps 2013
 */
trait HBaseEventConverter {

  val conf = new HBaseConfiguration()
  val admin = new HBaseAdmin(conf)

  val eventsTable = new HTable(conf, "events")

  val dataFamily = Bytes.toBytes("data")

  implicit def toBytes(x: Any): Array[Byte] = x match {
    case s: String => Bytes.toBytes(s)
    case evType: EventType.Value => Bytes.toBytes(evType.id)
    case p: BigDecimal => Bytes.toBytes(new java.math.BigDecimal(p.toString()))
    case l: Long => Bytes.toBytes(l)
  }

  implicit def toEventType(raw: Array[Byte]): EventType.Value = EventType(Bytes.toInt(raw))
  implicit def toString(raw: Array[Byte]): String = Bytes.toString(raw)
  implicit def toLong(raw: Array[Byte]): Long = Bytes.toLong(raw)
  implicit def toBigDecimal(raw: Array[Byte]): BigDecimal = Bytes.toBigDecimal(raw)

  //TODO: parameterize the following to avoid duplication

  implicit def toOptionString(raw: Option[Array[Byte]]): Option[String] = raw match {
    case Some(bytes) => Some(toString(bytes))
    case None => None
  }

  implicit def toOptionLong(raw: Option[Array[Byte]]): Option[Long] = raw match {
    case Some(bytes) => Some(toLong(bytes))
    case None => None
  }

  implicit def toOptionBigDecimal(raw: Option[Array[Byte]]): Option[BigDecimal] = raw match {
    case Some(bytes) => Some(toBigDecimal(bytes))
    case None => None
  }

  /**
   * Get the HBase key for an event.  The key design is:
   *
   *   <tiCode> 0 <timeStamp> <messageSequenceNumber>
   *
   * The "0" separator allows for partial key scans on tiCode (ie a particular asset).
   *
   * @param event  The event to generate a key for
   * @return       An array of bytes representing the HBase key.
   */
  def getKey(event: Event): Array[Byte] = {
    Bytes.add(event.tiCode + "0", event.timeStamp, event.messageSequenceNumber)
  }

}

