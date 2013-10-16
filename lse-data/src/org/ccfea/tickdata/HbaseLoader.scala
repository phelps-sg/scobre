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

  def toEventType(raw: Array[Byte]): EventType.Value = EventType(Bytes.toInt(raw))

  def getAssetId(tiCode: String): Long = {
    tiCode.substring(2).toLong
  }

  def getTiCode(assetId: Long): String = {
    "GB" + assetId.toString()
  }

  def getKey(event: Event): Array[Byte] = {
    Bytes.add(Bytes.toBytes(getAssetId(event.tiCode)),
      Bytes.toBytes(event.messageSequenceNumber))
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

  def convert(event: Event): Put = {

    implicit val timeStamp = event.timeStamp
    implicit val put: Put = new Put(getKey(event))

    store("eventType", event.eventType)
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
    val data: Seq[Put] = for(ev <- parsedEvents) yield convert(ev)
    eventsTable.put(data)
    parsedEvents.length
  }
}

class HBaseOrderReplay(selectedAsset: String, withGui: Boolean, maxNumEvents: Option[Int]) extends OrderReplay(selectedAsset, withGui, maxNumEvents) with HBaseEventConverter {

  def retrieveEvents() = {
    val keyStart = Bytes.toBytes(getAssetId(selectedAsset))
    val keyEnd = Bytes.toBytes(getAssetId(selectedAsset)+1)
    val scan: Scan = new Scan(keyStart, keyEnd)
    scan.addFamily(dataFamily)
    scan.setCaching(2000)
    val resultScanner: ResultScanner = eventsTable.getScanner(scan)
    for(result <- resultScanner) {
      println("=================")
      val col = result.getColumn(dataFamily, "eventType")
      println("Size = " + col.size())
      for(kv <- col) {
        println("key = " + kv.getKey)
        println("value = " + toEventType(kv.getValue))
        println("timestamp = " + new java.util.Date(kv.getTimestamp))
      }

    }
    new mutable.LinkedList()
  }

}



