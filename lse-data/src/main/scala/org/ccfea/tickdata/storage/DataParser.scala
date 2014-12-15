package org.ccfea.tickdata.storage

import org.ccfea.tickdata.storage.rawdata.HasDateTime
import org.ccfea.tickdata.event.Event

/**
 * (C) Steve Phelps 2014
 */
trait DataParser {

  def toRecord(values: Array[Option[String]], lineNumber: Long): HasDateTime

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
