package org.ccfea.tickdata

import java.text.DateFormat
import java.util.Date

/**
 * (C) Steve Phelps 2014
 */
trait ReplayApplication {

  def parseDate(date: Option[String]): Option[Date] = date match {
    case None => None
    case Some(dateStr) =>  Some(DateFormat.getDateInstance(DateFormat.SHORT).parse(dateStr))
  }

  implicit def AnyRefToOptionAnyVal(x: AnyRef): Option[AnyVal] = x match {
    case Some(double: Double) => Some(double)
    case Some(long: Long) => Some(long)
    case None => None
  }

  def main(args:Array[String])

}
