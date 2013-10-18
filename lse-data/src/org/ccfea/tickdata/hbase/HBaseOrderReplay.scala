package org.ccfea.tickdata.hbase

import org.apache.hadoop.hbase.client.{ResultScanner, Scan, Result}
import org.apache.hadoop.hbase.util.Bytes
import org.ccfea.tickdata.{Event, AbstractOrderReplay}
import collection.JavaConversions._

/**
 * (c) Steve Phelps 2013
 */
class HBaseOrderReplay(selectedAsset: String, withGui: Boolean, maxNumEvents: Option[Int]) extends AbstractOrderReplay(selectedAsset, withGui, maxNumEvents) with HBaseEventConverter {

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



