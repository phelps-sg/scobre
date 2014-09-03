package org.ccfea.tickdata.storage.rawdata

import java.text.SimpleDateFormat

/**
 * Provides a function for calculating a time-stamp value in milliseconds as a Long from
 * date and time fields.
 *
 * (c) Steve Phelps 2013
 */

trait HasDateTime {

  // without milliseconds
  val dfShort = new SimpleDateFormat("ddMMyyyy HH:mm:ss")

  // Time stamp format with milliseconds
  val df = new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS")

  def date: String
  def time: String

  def timeStamp: Long = {
    val dateTime = date + " " + time;
    if (time.length > 8 && time.contains(".")) {
      df.parse(dateTime).getTime()
    } else {
      dfShort.parse(dateTime).getTime()
    }
  }
}

