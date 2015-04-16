package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.client.{ResultScanner, Scan, Result}
import org.apache.hadoop.hbase.util.Bytes
import collection.JavaConversions._
import org.ccfea.tickdata.event.TickDataEvent
import java.util.Date
import grizzled.slf4j.Logger

/**
 * Retrieve time-sorted events for a selected asset from Apache HBase.
 * (c) Steve Phelps 2013
 */
class HBaseRetriever(val selectedAsset: String,
                      val startDate: Option[Date] = None, val endDate: Option[Date] = None, val cacheSize: Int = 1000)
    extends HBaseEventConverter with Iterable[TickDataEvent] {

  val partialKeyScan = new DateKeyRange(selectedAsset, startDate, endDate, cacheSize)

  val scanner = partialKeyScan.scanner

  def iterator: Iterator[TickDataEvent] = {
    new EventIterator(scanner)
  }

}



