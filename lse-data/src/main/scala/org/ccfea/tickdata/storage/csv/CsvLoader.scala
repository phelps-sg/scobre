package org.ccfea.tickdata.storage.csv

import java.io.{BufferedReader, InputStreamReader, FileInputStream}
import grizzled.slf4j.Logger
import org.ccfea.tickdata.storage.rawdata.lse.{TradeReportRaw, OrderHistoryRaw, OrderDetailRaw}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import org.ccfea.tickdata.storage.DataLoader
import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * Read and parse the raw data from CSV files.
 *
 * (c) Steve Phelps 2013
 */
trait CsvLoader extends DataLoader {

  /**
   * The name of the file containing the raw CSV data.
   */
  val fileName: String

  val separator: Char = ','
  val quote: Char = '"'
  val escape: Char = '\\'

  override val logger: Logger = Logger("org.ccfea.tickdata.storage.csv.CsvLoader")

  def run = {
    val reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName) ))
    val events = new ListBuffer[HasDateTime]
    try {
      var next = true
      var lineNumber = 0
      while (next) {
        val line = reader.readLine()
        lineNumber = lineNumber + 1
        if (line != null) {
          if (!(line.charAt(0) == '#')) {
            try {
              val event = toRecord(parse(line), lineNumber)
              events += event
            } catch {
              case e: Exception =>
                logger.error("Parse error at line number " + lineNumber + ":")
                logger.error(line)
//                logger.error(e.getMessage)
                e.printStackTrace()
                throw new IllegalArgumentException(e)
            }
            if (events.size > batchSize) {
              parseAndInsert(events)
            }
          }
        } else {
          next = false
        }
      }
    } finally {
      // Parse remaining events in buffer
      parseAndInsert(events)
      reader.close()
    }
  }

  def parseAndInsert(events: ListBuffer[HasDateTime]) = {
    val parsed = events.par.map(parseEvent)
    insertData(parsed.seq)
    events.clear()
  }

  def parse(line: String): Array[Option[String]] = {
    val values = Array.newBuilder[Option[String]]
    val buffer = new StringBuilder
    var insideQuotes = false
    var escapeNext = false
    for (c <- line) {
      if (escapeNext) { buffer += c; escapeNext = false }
      else if (c == escape) escapeNext = true
      else if (c == quote) insideQuotes = !insideQuotes
      else if (c == separator && !insideQuotes) {
        values += parseNull(buffer.result())
        buffer.clear()
      }
      else buffer += c
    }
    values += parseNull(buffer.result())
    values.result()
  }

  def parseNull(value: String): Option[String] = {

    value match {

      case "NULL" => None

      // At some points in the LSE data the empty-string is also used to denote NULL values
      case "" => None

      case x => Some(x)
    }
  }

  def toRecord(values: Array[Option[String]], lineNumber: Long): HasDateTime

}
