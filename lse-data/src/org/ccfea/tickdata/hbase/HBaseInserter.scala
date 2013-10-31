package org.ccfea.tickdata.hbase

import org.apache.hadoop.hbase.client.Put
import org.ccfea.tickdata.Event
import collection.JavaConversions._

/**
 * Store events in Apache HBase.
 *
 * (c) Steve Phelps 2013
 */

trait HBaseInserter extends HBaseEventConverter {

  def store(field: String, data: Array[Byte])(implicit put: Put, timeStamp: Long) {
    put.add(dataFamily, toBytes(field), timeStamp, data)
  }

  def store(field: String, data: Option[Any])(implicit put: Put, timeStamp: Long) {
    data match {
      case Some(data) =>
        store(field, toBytes(data))
      case None =>
      // no need to store
    }
  }

  implicit def convert(event: Event): Put = {

    implicit val timeStamp = event.timeStamp
    implicit val put: Put = new Put(getKey(event))

    store("eventType", event.eventType)
    store("marketSegmentCode", event.marketSegmentCode)
    store("currencyCode", event.currencyCode)
    store("marketMechanismType", event.marketMechanismType)
    store("aggregateSize", event.aggregateSize)
    store("tradeDirection", event.tradeDirection)
    store("orderCode", event.orderCode)
    store("tradeSize", event.tradeSize)
    store("broadcastUpdateAction", event.broadcastUpdateAction)
    store("marketSectorCode", event.marketSectorCode)
    store("marketMechanismGroup", event.marketMechanismGroup)
    store("price", event.price)
    store("singleFillInd", event.singleFillInd)
    store("matchingOrderCode", event.matchingOrderCode)
    store("resultingTradeCode", event.resultingTradeCode)
    store("tradeCode", event.tradeCode)
    store("tradeTimeInd", event.tradeTimeInd)
    store("convertedPriceInd", event.convertedPriceInd)

    put
  }

  def insertData(parsedEvents: Seq[Event]): Int = {
    eventsTable.put(parsedEvents.map(convert))
    parsedEvents.length
  }
}
