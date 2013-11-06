package org.ccfea.tickdata.storage.sql

import scala.slick.driver.MySQLDriver.simple._
import RelationalTables.events
import org.ccfea.tickdata.event.Event

// Use the implicit threadLocalSession
import Database.threadLocalSession

/**
 * Functionality for inserting the parsed events into a SQL database.
 *
 * (c) Steve Phelps 2013
 */
trait SqlInserter {

  def insertData(parsedEvents: Seq[Event]): Int = {
    events.insertAll(parsedEvents.seq: _*) match {
      case Some(x: Int) => x
      case _ =>
        throw
          new UnsupportedOperationException("Unsupported database")
    }
  }

}

