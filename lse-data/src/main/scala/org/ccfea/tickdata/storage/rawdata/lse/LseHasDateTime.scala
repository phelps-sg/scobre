package org.ccfea.tickdata.storage.rawdata.lse

import java.text.SimpleDateFormat

import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * Provides a function for calculating a time-stamp value in milliseconds as a Long from
 * date and time fields.
 *
 * (c) Steve Phelps 2013
 */
trait LseHasDateTime extends HasDateTime {

  val dfMs = new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS")

  override def timeStamp: Long = {
    if (time.length > 8 && time.contains(".")) {
      dfMs.parse(date + " " + time).getTime
    } else {
      super.timeStamp
    }
  }
}

