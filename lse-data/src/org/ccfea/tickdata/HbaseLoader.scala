package org.ccfea.tickdata.hbase

import org.ccfea.tickdata._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{KeyValue, HBaseConfiguration}

import collection.JavaConversions._
import scala.collection.mutable
import org.ccfea.tickdata.Event

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
//
//  def getAssetId(tiCode: String): Long = {
//    tiCode.substring(2).toLong
//  }
//
//  def getTiCode(assetId: Long): String = {
//    "GB" + assetId.toString()
//  }

  def getKey(event: Event): Array[Byte] = {
    Bytes.add(event.tiCode + "0", event.timeStamp, event.messageSequenceNumber)
  }

}

class HBaseLoader(batchSize: Int = 2000, url: String, driver: String) extends SqlLoader(batchSize, url, driver) with HBaseEventConverter {

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
    store("marketMechanismType", event.marketMechanismType)
    store("aggregateSize", event.aggregateSize)
    store("buySellInd", event.buySellInd)
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

  override def insertData(parsedEvents: Seq[Event]): Int = {
    eventsTable.put(parsedEvents.map(convert))
    parsedEvents.length
  }
}

class HBaseOrderReplay(selectedAsset: String, withGui: Boolean, maxNumEvents: Option[Int]) extends OrderReplay(selectedAsset, withGui, maxNumEvents) with HBaseEventConverter {

  def cacheSize: Int = 2000

  def getColumn(result: Result, name: String): Option[Array[Byte]] = {
    val column =  result.getColumn(dataFamily, name)
    column.size match {
      case 0 => None
      case 1 => Some(column(0).getValue)
      case _ => throw new IllegalArgumentException("More than one result in column " + name)
    }
  }

  def getTimeStamp(result: Result): Long = {
    val column = result.getColumn(dataFamily, "eventType")
    assert(column.size == 1)
    column(0).getTimestamp
  }

  def getMessageSequenceNumber(result: Result): Long = {
    Bytes.toLong(Bytes.tail(result.getRow, 8))
  }

  def retrieveEvents() = {
    // Setup partial key-scan on selectedAsset
    val keyStart = Bytes.toBytes(selectedAsset + "0")
    val keyEnd = Bytes.toBytes(selectedAsset + "1")
    val scan: Scan = new Scan(keyStart, keyEnd)
    scan.addFamily(dataFamily)
    scan.setCaching(cacheSize)
    val resultScanner: ResultScanner = eventsTable.getScanner(scan)
    for(r <- resultScanner)
     yield new Event(None,
                        getColumn(r, "eventType").get,
                        getMessageSequenceNumber(r),
                        getTimeStamp(r),
                        selectedAsset,
                        getColumn(r, "marketSegmentCode").get,
                        getColumn(r, "marketMechanismType"),
                        getColumn(r, "aggregateSize"),
                        getColumn(r, "buySellInd"),
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
}



