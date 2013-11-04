package org.ccfea.tickdata.event

/**
 * Super-class of all order-replay events.
 *
 * (C) Steve Phelps 2013
 */
abstract class OrderReplayEvent {
  def timeStamp: Long
  def messageSequenceNumber: Long
  def tiCode: String
}


