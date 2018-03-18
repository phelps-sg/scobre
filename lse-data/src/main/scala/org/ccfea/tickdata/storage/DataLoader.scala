package org.ccfea.tickdata.storage

import grizzled.slf4j.Logger
import org.ccfea.tickdata.storage.dao.Event
import org.ccfea.tickdata.storage.rawdata._

/**
 * Parse the raw data and convert it to a sequence of tick Events.
 *
 * (c) Steve Phelps 2013
 */

trait DataLoader {

  val logger = Logger(classOf[DataLoader])

  val batchSize: Int

  def parser: DataParser

  def run(): Unit

  def insertData(parsedEvents: Seq[Event]): Int

  def parseEvent(rawEvent: HasDateTime): Event = parser.parseEvent(rawEvent)

  def toRecord(values: Array[Option[String]], lineNumber: Long): HasDateTime = parser.toRecord(values, lineNumber)

}

