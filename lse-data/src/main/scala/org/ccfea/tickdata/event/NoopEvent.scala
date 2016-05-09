package org.ccfea.tickdata.event

import java.util.Date

/**
  * An event in which no change of state is specified.
  */
case class NoopEvent(timeStamp: Date, messageSequenceNumber: Long, tiCode: String) extends TickDataEvent
