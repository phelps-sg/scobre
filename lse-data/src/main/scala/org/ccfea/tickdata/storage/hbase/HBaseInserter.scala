package org.ccfea.tickdata.storage.hbase

import grizzled.slf4j.Logger
import org.ccfea.tickdata.storage.dao.{Event, EventType}

import collection.JavaConversions._
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes

/**
 * Store a sequence of {@link org.ccfea.tickdata.storage.dao.Event} objects in an Apache HBase table.
 *
 * (c) Steve Phelps 2015
 */
trait HBaseInserter extends HBaseEventConverter with HBaseEventsTable {

  /**
   * An internal message-sequence number (MSN) which is appended to time-stamps to
   * prevent row-key collisions for low-resolution data.
   */
  var msn: Int = 0

  val MSN_MODULO = 1000

  val logger = Logger(classOf[HBaseInserter])

  //TODO
//  val NUM_SALT_BUCKETS = 100

  /**
   * The column names of the events table.  These also correspond with the
   * attributes of {@link org.ccfea.tickdata.storage.dao.Event}.
   */
  val fields: List[String] = List("eventType", "marketSegmentCode", "currencyCode", "marketMechanismType",
    "aggregateSize", "tradeDirection", "orderCode", "tradeSize", "broadcastUpdateAction", "marketSectorCode",
    "marketMechanismGroup", "price", "singleFillInd", "matchingOrderCode", "resultingTradeCode", "tradeCode",
    "tradeTimeInd", "convertedPriceInd")

  def store(field: String, data: Array[Byte])(implicit put: Put, timeStamp: Long) {
    put.addColumn(dataFamily, toBytes(field), timeStamp, data)
  }

  def store(field: String, data: AnyRef)(implicit put: Put, timeStamp: Long) {
    data match {
      case Some(data) =>
        store(field, toBytes(data))
      case None =>
      // no need to store
      case data =>
        store(field, toBytes(data))
    }
  }

  def internalMessageSequenceNumber(): Int = {
    // Add private MSN to ensure that there are no row-key collisions- important for LSE data
    msn = (msn + 1) % MSN_MODULO
    msn
  }

  /**
   * Convert a single {@link org.ccfea.tickdata.storage.dao.Event} object into an
   * HBase Put object.
   *
   * @param event   A single tick.
   * @return        An HBase Put object which can be inserted into the database.
   */
  implicit def convert(event: Event): Put = {
    logger.debug("Inserting " + event)
    implicit val timeStamp = event.timeStamp
    implicit val put: Put = new Put(getKey(event, internalMessageSequenceNumber()))
    def getField(f: String): AnyRef = classOf[Event].getMethod(f).invoke(event)
    for(field <- fields) store(field, getField(field))
    put
  }

  /**
   * Insert a sequence of ticks into the database.
   *
   * @param parsedEvents    A sequence of ticks.
   * @return                The total number of ticks successfully inserted
   *                        into the events table.
   */
  def insertData(parsedEvents: Seq[Event]): Int = {
    eventsTable.put( for(event <- parsedEvents) yield convert(event) )
    parsedEvents.length
  }
}
