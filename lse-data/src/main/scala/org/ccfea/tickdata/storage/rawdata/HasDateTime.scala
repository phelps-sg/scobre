package org.ccfea.tickdata.storage.rawdata

import java.text.SimpleDateFormat

/**
 * Provides a function for calculating a time-stamp value in milliseconds as a Long from
 * date and time fields.
 *
 * (c) Steve Phelps 2013
 */
trait HasDateTime {

  val df = new SimpleDateFormat("ddMMyyyy HH:mm:ss")

  def date: String
  def time: String

  def timeStamp: Long =  df.parse(date + " " + time).getTime

}

