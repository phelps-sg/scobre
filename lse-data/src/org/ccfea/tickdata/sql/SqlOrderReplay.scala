package org.ccfea.tickdata.sql

import scala.slick.driver.MySQLDriver.simple._
import org.ccfea.tickdata.{Event, AbstractOrderReplay}
import RelationalTables.events
// Use the implicit threadLocalSession
import Database.threadLocalSession

/**
 * Replay events which are stored in a SQL database.
 *
 * (c) Steve Phelps 2013
 */
class SqlOrderReplay(val url: String, val driver: String, selectedAsset: String, withGui: Boolean, maxNumEvents: Option[Int]) extends AbstractOrderReplay(selectedAsset, withGui, maxNumEvents) {

  def retrieveEvents(): Seq[Event] = {

    Database.forURL(url, driver = driver) withSession {

      val eventsForSingleAsset = for {
        event <- events
        if (event.tiCode === selectedAsset)
      } yield event

      val allEventsByTime =
        eventsForSingleAsset.sortBy(_.messageSequenceNumber).sortBy(_.timeStamp)

      val selectedEvents = maxNumEvents match {
        case Some(n) => allEventsByTime.take(n)
        case None    => allEventsByTime
      }

      selectedEvents.list
    }
  }
}

