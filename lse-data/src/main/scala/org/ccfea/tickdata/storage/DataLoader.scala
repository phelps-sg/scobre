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

  implicit def toOptionBigDecimal(x: Option[String]): Option[BigDecimal] = {
    x match {
      case Some(x) => Some(BigDecimal(x))
      case None => None
    }
  }
  implicit def optionToLong(x: Option[String]): Long = x.get.toLongExact
  implicit def optionToOptionLong(x: Option[String]): Option[Long] =
    x match {
      case Some(y:String) => Some(optionToLong(x))
      case None => None
    }
  implicit def toLong(x: String): Long = x.toLongExact
  implicit def toBigDecimal(x: String): BigDecimal = BigDecimal(x)

}

