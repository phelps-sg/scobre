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

  /**
   * Date/time format which includes milliseconds.
   */
  val dfMs = new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS")

  override def timeStamp: Long = {
    // The LSE data changes from a resolution of seconds to milliseconds midway through.
    if (time.length > 8 && time.contains(".")) {
      dfMs.parse(date + " " + time).getTime
    } else {
      super.timeStamp
    }
  }
}

