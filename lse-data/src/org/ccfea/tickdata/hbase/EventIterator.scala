package org.ccfea.tickdata.hbase

import org.apache.hadoop.hbase.client.{Result, ResultScanner}
import collection.JavaConversions._
import org.ccfea.tickdata.event.{OrderReplayEvent, Event}

class EventIterator(val scanner: ResultScanner) extends Iterator[OrderReplayEvent] with HBaseEventConverter {

  val resultIterator: Iterator[Result] = scanner.iterator()

  def hasNext: Boolean = resultIterator.hasNext

  def next(): OrderReplayEvent = resultIterator.next().toOrderReplayEvent

}
