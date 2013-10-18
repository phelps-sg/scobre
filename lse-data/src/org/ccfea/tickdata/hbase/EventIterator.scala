package org.ccfea.tickdata.hbase

import org.ccfea.tickdata.Event
import org.apache.hadoop.hbase.client.{Result, ResultScanner}
import collection.JavaConversions._

class EventIterator(val scanner: ResultScanner) extends Iterator[Event] with HBaseEventConverter {

  def resultIterator: Iterator[Result] = scanner.iterator()

  def hasNext: Boolean = resultIterator.hasNext

  def next(): Event = resultIterator.next()
}
