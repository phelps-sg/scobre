package org.ccfea.tickdata.event

/**
 * (C) Steve Phelps 2013
 */
case class OrderFilledEvent(val timeStamp: Long, val messageSequenceNumber: Long,
                            val tiCode: String, val orderCode: String) extends OrderCodeEvent

