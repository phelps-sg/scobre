package org.ccfea.tickdata.event

import java.util.Date

/**
 * BroadcastUpdateAction == 'F'
 *
 * (c) Steve Phelps 2013
 */
case class StartOfDataMarker(timeStamp: Date, messageSequenceNumber: Long, tiCode: String) extends OrderReplayEvent
