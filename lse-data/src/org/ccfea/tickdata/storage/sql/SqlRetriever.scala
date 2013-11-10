package org.ccfea.tickdata.storage.sql

import scala.slick.driver.MySQLDriver.simple._
import RelationalTables.events
import org.ccfea.tickdata.event.{OrderReplayEvent, Event}
import org.ccfea.tickdata.simulator.OrderReplayer

// Use the implicit threadLocalSession
import Database.threadLocalSession

/**
 * Retrieve time-sorted events for the selected asset from a SQL database.
 *
 * (c) Steve Phelps 2013
 */
trait SqlRetriever { //TODO extends Iterable[OrderReplayEvent] {

  def selectedAsset: String
  def url: String
  def driver: String
  def maxNumEvents: Option[Int]

  def retrieveEvents(): Iterable[Event] = {

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

  //TODO
//  def iterator: Iterable[OrderReplayEvent] = {
//    val evs = retrieveEvents().iterator
//    new Iterable[OrderReplayEvent] {
//      def iterator = new Iterator[OrderReplayEvent] {
//        def next = evs.next.toOrderReplayEvent
//        def hasNext = evs.hasNext
//      }
//    }
//  }
}

