package org.ccfea.tickdata.storage

import org.ccfea.tickdata.storage.dao.Event
import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * Parse raw data into a format suitable for replaying through a
 * {@link org.ccfea.simulator.MarketSimulator}.
 *
 * (C) Steve Phelps 2015
 */
trait DataParser {

  /**
   * Convert a single row of the raw data into a tuple containing a time-stamp.
   * This is used to convert raw column-based input into a case-class with
   * typed attributes.
   *
   * @param values      The raw data of the row represented as an array whose
   *                    elements corresponds to columns.
   * @param lineNumber  The row number which is used for error-reporting.
   * @return  A case class of type HasDateTime representing the parsed data.
   */
  def toRecord(values: Array[Option[String]], lineNumber: Long): HasDateTime

  /**
   * Convert the data returned from the toRecord method into a canonical
   * {@link org.ccfea.tickdata.storage.dao.Event} object.
   *
   * @param rawEvent A single time-stamped tuple representing the parsed-raw input.
   * @return  A single instance of Event.
   */
  def parseEvent(rawEvent: HasDateTime): Event

  implicit def toOptionBigDecimal(x: Option[String]): Option[BigDecimal] = {
    x match {
      case Some(x) => Some(BigDecimal(x))
      case None => None
    }
  }
  implicit def optionToLong(x: Option[String]): Long = x.get.toLongExact
  implicit def optionToOptionLong(x: Option[String]): Option[Long] =
    x match {
      case Some(y:String) => Some(optionToLong(x))
      case None => None
    }
  implicit def toLong(x: String): Long = x.toLongExact
  implicit def toBigDecimal(x: String): BigDecimal = BigDecimal(x)

}
