package org.ccfea.tickdata.sql

import scala.slick.driver.MySQLDriver.simple._
import org.ccfea.tickdata.Event
import RelationalTables.events
// Use the implicit threadLocalSession
import Database.threadLocalSession

/**
 * Functionality for inserting the parsed data into a SQL database.
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

