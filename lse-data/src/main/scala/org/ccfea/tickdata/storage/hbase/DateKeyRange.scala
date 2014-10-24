package org.ccfea.tickdata.storage.hbase

import java.util.Date
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Scan

/**
 * (C) Steve Phelps 2013
 */
class DateKeyRange(val tiCode: String, val startDate: Option[Date], val endDate: Option[Date], val cacheSize: Int)
    extends HBaseEventConverter {

  val keyStart = generateScanKey(startDate, true)
  val keyEnd = generateScanKey(endDate, false)

  val scan: Scan = new Scan(keyStart, keyEnd)
  scan.addFamily(dataFamily)
  scan.setCaching(cacheSize)

  val scanner = eventsTable.getScanner(scan)

  def generateScanKey(date: Option[Date], isStart: Boolean) = date match {
    case Some(date) => Bytes.add(pad(tiCode) + "0", Bytes.toBytes(date.getTime), 0L)
    case None => Bytes.toBytes(pad(tiCode) + (if (isStart) "0" else "1"))
  }
}
