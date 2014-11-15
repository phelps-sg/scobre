package org.ccfea.tickdata.event

/**
 * (c) Steve Phelps 2013
 */
case class MultipleEvent(val events: Seq[TickDataEvent]) extends TickDataEvent {
  def timeStamp = events.head.timeStamp
  def messageSequenceNumber = events.head.messageSequenceNumber
  def tiCode = events.head.tiCode
}
