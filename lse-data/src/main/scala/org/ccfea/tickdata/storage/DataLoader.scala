package org.ccfea.tickdata.storage

import rawdata._
import grizzled.slf4j.Logger
import org.ccfea.tickdata.event.{EventType, Event}
import org.ccfea.tickdata.storage.rawdata.lse.{OrderDetailRaw, OrderHistoryRaw, TradeReportRaw}
import org.ccfea.tickdata.order.{MarketMechanismType, TradeDirection}

/**
 * Parse the raw data and convert it to a sequence of tick Events.
 *
 * (c) Steve Phelps 2013
 */

trait DataLoader {

  val logger = Logger(classOf[DataLoader])

  val batchSize: Int

  def run(): Unit

  def insertData(parsedEvents: Seq[Event]): Int

  def parseEvent(rawEvent: HasDateTime): Event
}

