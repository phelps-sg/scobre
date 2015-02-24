package org.ccfea.tickdata.storage.test

import org.ccfea.tickdata.storage.dao.Event

/**
 * (C) Steve Phelps 2014
 */
trait TestInserter {

  def insertData(parsedEvents: Seq[Event]): Int = {
    for(event <- parsedEvents) {
      println(event)
    }
    parsedEvents.length
  }
}
