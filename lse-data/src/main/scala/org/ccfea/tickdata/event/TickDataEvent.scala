package org.ccfea.tickdata.event

import java.util.Date

/**
 * Super-class of all order-replay events.
 *
 * (C) Steve Phelps 2013
 */
abstract class TickDataEvent {
  def timeStamp: Date
  def messageSequenceNumber: Long
  def tiCode: String
}


