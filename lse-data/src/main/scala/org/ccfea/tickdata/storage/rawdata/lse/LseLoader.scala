package org.ccfea.tickdata.storage.rawdata.lse

import grizzled.slf4j.Logger
import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * (C) Steve Phelps 2014
 */
trait LseLoader {

  val logger = Logger("org.ccfea.tickdata.storage.rawdata.lse.LseLoader")

  /**
   * One of "order_history_raw", "order_detail_raw" or "trade_reports_raw" to indicate the
   * source of the data.
   */
  val recordType: String

  def toRecord(values: Array[Option[String]]): HasDateTime = {

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

    var i = 0
    def next: Option[String] = {
      val result = values(i)
      i = i+1
      result
    }

    try {

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
    } catch {
      case nse: NoSuchElementException =>
        nse.printStackTrace()
        throw new IllegalArgumentException("Could not parse record: missing field in column " + i)
      case e: Exception =>
        e.printStackTrace()
        throw new IllegalArgumentException(e.getMessage)
    }
  }


}
