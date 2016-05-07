package org.ccfea.tickdata

import java.text.DateFormat
import java.util.Date

/**
  * Created by sphelps on 06/05/16.
  */
trait ScobreApplication {

 /**
   * Parse a date supplied as a command-line option.
   *
   * @param date  An optional date in the standard java short format.
   *
   * @return  An optional java.util.Date instance.
   */
  def parseDate(date: Option[String]): Option[Date] = date match {
    case None => None
    case Some(dateStr) =>  Some(DateFormat.getDateInstance(DateFormat.SHORT).parse(dateStr))
  }

}
