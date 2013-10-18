package org.ccfea.tickdata.hbase

import org.apache.hadoop.hbase.client.{ResultScanner, Scan, Result}
import org.apache.hadoop.hbase.util.Bytes
import org.ccfea.tickdata.{Event, AbstractOrderReplay}
import collection.JavaConversions._


/**
 * Retrieve time-sorted events for a selected asset from Apache HBase.
 * (c) Steve Phelps 2013
 */
trait HBaseRetriever extends HBaseEventConverter with Iterable[Event] {

  def selectedAsset: String

  def cacheSize: Int = 20000

  val keyStart = Bytes.toBytes(selectedAsset + "0")
  val keyEnd = Bytes.toBytes(selectedAsset + "1")
  val scan: Scan = new Scan(keyStart, keyEnd)
  scan.addFamily(dataFamily)
  scan.setCaching(cacheSize)
  val scanner = eventsTable.getScanner(scan)

  def iterator: Iterator[Event] = {
    new EventIterator(scanner)
  }


  def retrieveEvents() = {
    for(r <- scanner) yield toEvent(r)
  }
}



