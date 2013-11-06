package org.ccfea.tickdata.storage.csv

import java.io.{BufferedReader, InputStreamReader, FileInputStream}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import org.ccfea.tickdata.storage.DataLoader
import org.ccfea.tickdata.storage.rawdata.{OrderDetailRaw, TradeReportRaw, OrderHistoryRaw, HasDateTime}

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

  /**
   * One of "order_history_raw", "order_detail_raw" or "trade_reports_raw" to indicate the
   * source of the data.
   */
  val recordType: String

  val separator: Char = ','
  val quote: Char = '"'
  val escape: Char = '\\'

  def run = {
    val reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName) ))
    val events = new ListBuffer[HasDateTime]
    try {
      var next = true
      while (next) {
        val line = reader.readLine()
        if (line != null) {
          val event = toRecord(parse(line))
          events += event
          if (events.size > batchSize) {
            parseAndInsert(events)
          }
        }
        else next = false
      }
    } finally {
      // Parse remaining events in buffer
      parseAndInsert(events)
      reader.close()
    }
  }

  def parseAndInsert(events: ListBuffer[HasDateTime]) = {
    val parsed = events.par.map(parseEvent(_))
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
        values += parseNull(buffer.result)
        buffer.clear
      }
      else buffer += c
    }
    values += parseNull(buffer.result)
    return values.result
  }

  def parseNull(value: String): Option[String] = {

    value match {

      case "NULL" => None

      // At some points in the LSE data the empty-string is also used to denote NULL values
      case "" => None

      case x => Some(x)
    }
  }

  def toRecord(values: Array[Option[String]]): HasDateTime = {

    implicit def toOptionBigDecimal(x: Option[String]): Option[BigDecimal] = {
      x match {
        case Some(x) => Some(BigDecimal(x))
        case None => None
      }
    }
    implicit def optionToLong(x: Option[String]): Long = x.get.toLongExact
    implicit def toLong(x: String): Long = x.toLongExact
    implicit def toBigDecimal(x: String): BigDecimal = BigDecimal(x)

    var i = 0
    def next: Option[String] = {
      val result = values(i)
      i = i+1
      result
    }


    recordType match {

      case "order_history_raw" =>
        i = 0
        new OrderHistoryRaw(orderCode = next.get,
                              orderActionType = next.get,
                              matchingOrderCode = next,
                              tradeSize = next,
                              tradeCode = next,
                              tiCode = next.get,
                              countryofRegister = next.get,
                              currencyCode = next.get,
                              marketSegmentCode = next.get,
                              aggregateSize = next.get,
                              buySellInd = next.get,
                              marketMechanismType = next.get,
                              messageSequenceNumber = next,
                              date = next.get,
                              time = next.get
        )

      case "trade_reports_raw" =>
        i = 0
        new TradeReportRaw(messageSequenceNumber = next,
                            tiCode = next.get,
                            marketSegmentCode = next.get,
                            countryOfRegister = next.get,
                            currencyCode = next.get,
                            tradeCode = next,
                            tradePrice = next,
                            tradeSize = next,
                            date = next.get,
                            time = next.get,
                            broadcastUpdateAction = next.get,
                            tradeTypeInd = next.get,
                            tradeTimeInd = next.get,
                            bargainConditions = next.get,
                            convertedPriceInd = next.get,
                            publicationDate = next.get,
                            publicationTime = next.get
                            )

      case "order_detail_raw" =>
        i = 0
        new OrderDetailRaw(orderCode = next.get,
                            marketSegmentCode = next.get,
                            marketSectorCode = next.get,
                            tiCode = next.get,
                            countryofRegister = next.get,
                            currencyCode = next.get,
                            participantCode = next,
                            buySellInd = next.get,
                            marketMechanismGroup = next.get,
                            marketMechanismType = next.get,
                            price = next.get,
                            aggregateSize = next.get,
                            singleFillInd = next.get,
                            broadcastUpdateAction = next.get,
                            date = next.get,
                            time = next.get,
                            messageSequenceNumber = next.get)
    }
  }

}
