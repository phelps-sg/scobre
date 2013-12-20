package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.client.{ResultScanner, Scan, Result}
import org.apache.hadoop.hbase.util.Bytes
import collection.JavaConversions._
import org.ccfea.tickdata.event.{OrderReplayEvent, Event}
import java.util.Date
import grizzled.slf4j.Logger


/**
 * Retrieve time-sorted events for a selected asset from Apache HBase.
 * (c) Steve Phelps 2013
 */
class HBaseRetriever(val cacheSize: Int = 1000, val selectedAsset: String, val startDate: Option[Date] = None, val endDate: Option[Date] = None) extends HBaseEventConverter with Iterable[OrderReplayEvent] {

  val logger = Logger(classOf[HBaseRetriever])
//  def cacheSize: Int = 1000
//  def selectedAsset: String
//  def startDate: Option[Date]
//  def endDate: Option[Date]

  val keyStart = generateScanKey(selectedAsset, startDate, true)
  val keyEnd = generateScanKey(selectedAsset, endDate, false)

  logger.debug("startDate = " + startDate)
  logger.debug("endDate = " + endDate)

  val scan: Scan = new Scan(keyStart, keyEnd)
  scan.addFamily(dataFamily)
  scan.setCaching(cacheSize)

  val scanner = eventsTable.getScanner(scan)

  def iterator: Iterator[OrderReplayEvent] = {
    new EventIterator(scanner)
  }

  def retrieveEvents() = {
    scanner.par.map(toEvent(_)).seq
//    for(r <- scanner) yield toEvent(r)
  }

}



