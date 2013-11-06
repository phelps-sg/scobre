package org.ccfea.tickdata.storage.sql

import scala.slick.driver.MySQLDriver.simple._
import org.ccfea.tickdata.storage.DataLoader
import org.ccfea.tickdata.storage.rawdata.HasDateTime

// Use the implicit threadLocalSession
import Database.threadLocalSession

/**
 * Functionality for importing the original raw data from Sql tables.
 *
 * (c) Steve Phelps 2013
 */
trait SqlLoader extends DataLoader {

  def url: String
  def driver: String

  def run {
    Database.forURL(url = url, driver = driver) withSession {
      parseAndInsertData(Query(RawTables.orderDetails))
      parseAndInsertData(Query(RawTables.orderHistory))
      parseAndInsertData(Query(RawTables.tradeReports))
    }
  }

  def parseAndInsertData(rawQuery: Query[Any, _ <: HasDateTime]) {
    println(rawQuery.selectStatement)
    var finished = false
    var offset = 0
    do {
      val shortQuery = rawQuery.drop(offset).take(batchSize)
      finished = shortQuery.list.length < batchSize
      val parsed = shortQuery.list.par.map(parseEvent(_))
      val numRows = insertData(parsed.seq)
      offset = offset + numRows
    } while (!finished)
    println("done.")
  }

}
