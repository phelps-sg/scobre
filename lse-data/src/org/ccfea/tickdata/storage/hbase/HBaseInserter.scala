package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.client.Put
import collection.JavaConversions._
import org.ccfea.tickdata.event.{EventType, Event}

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

  def toTransactionMapPut(event: Event): Put = {
    implicit val put = new Put(event.resultingTradeCode.get)
    implicit val timeStamp = event.timeStamp

    store("matchingOrderCode", event.matchingOrderCode)
    store("orderCode", event.orderCode)

    put
  }

  def insertData(parsedEvents: Seq[Event]): Int = {

    eventsTable.put(
      for(event <- parsedEvents) yield convert(event)
    )

//    transactionsTable.put(
 //       for(event <- parsedEvents;
  //          if event.eventType == EventType.OrderMatched || event.eventType == EventType.OrderFilled)
   //       yield toTransactionMapPut(event)
    //)

    parsedEvents.length
  }
}
