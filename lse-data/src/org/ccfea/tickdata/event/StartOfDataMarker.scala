package org.ccfea.tickdata.event

import java.util.Date

/**
 * BroadcastUpdateAction == 'F'
 *
 * (c) Steve Phelps 2013
 */
case class StartOfDataMarker(val timeStamp: Date, val messageSequenceNumber: Long, val tiCode: String)
  extends OrderReplayEvent
