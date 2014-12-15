package org.ccfea.tickdata.storage.rawdata.lse

import grizzled.slf4j.Logger
import org.ccfea.tickdata.event.{EventType, Event}
import org.ccfea.tickdata.order.{TradeDirection, MarketMechanismType}
import org.ccfea.tickdata.storage.{DataParser, DataLoader}
import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * (C) Steve Phelps 2014
 */
trait LseLoader extends DataParser {

  val logger = Logger("org.ccfea.tickdata.storage.rawdata.lse.LseLoader")

  /**
   * One of "order_history_raw", "order_detail_raw" or "trade_reports_raw" to indicate the
   * source of the data.
   */
  val recordType: String

  def toRecord(values: Array[Option[String]], lineNumber: Long): HasDateTime = {

    var i = 0
    def next: Option[String] = {
      val result = values(i)
      i += 1
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


 /**
   * Convert a row of one of the three LSE tables (order_history, order_detail, trade_reports)
   * into an Event object which represents a tick.
   *
   * @param rawEvent  A tuple representation of the raw data in the current row being parsed.
   * @return  A tick whose eventType represents the type of event that has occurred.
   */
  def parseEvent(rawEvent: HasDateTime): Event = {

    logger.debug("Raw event = " + rawEvent)

    implicit def marketMechanismTypeToEnum(marketMechanismType: String): MarketMechanismType.Value = {
      marketMechanismType match {
        case "LO" => MarketMechanismType.LimitOrder
        case "MO" => MarketMechanismType.MarketOrder
        case _ =>    MarketMechanismType.Other
      }
    }

    implicit def orderActionTypeToEventType(orderActionType: String): EventType.Value = {
      orderActionType match {
        case "D" => EventType.OrderDeleted
        case "E" => EventType.OrderExpired
        case "P" => EventType.OrderMatched
        case "M" => EventType.OrderFilled
        case "T" => EventType.TransactionLimit
      }
    }

    implicit def buySellIndToTradeDirection(buySellInd: String): TradeDirection.Value = {
      buySellInd match {
        case "B" => TradeDirection.Buy
        case "S" => TradeDirection.Sell
      }
    }

    rawEvent match {

      case oh: OrderHistoryRaw =>

          Event(None, oh.orderActionType, oh.messageSequenceNumber,
            oh.timeStamp, oh.tiCode, oh.marketSegmentCode, oh.currencyCode,
            Some(oh.marketMechanismType), Some(oh.aggregateSize), Some(oh.buySellInd),
            Some(oh.orderCode), oh.tradeSize, None, None, None, None, None,
            oh.matchingOrderCode, oh.tradeCode,
            None, None, None)

      case od: OrderDetailRaw  =>

          Event(None, EventType.OrderSubmitted, od.messageSequenceNumber,
            od.timeStamp, od.tiCode, od.marketSegmentCode, od.currencyCode,
            Some(od.marketMechanismType), Some(od.aggregateSize), Some(od.buySellInd),
            Some(od.orderCode), None, Some(od.broadcastUpdateAction),
            Some(od.marketSectorCode), Some(od.marketMechanismGroup), Some(od.price), Some(od.singleFillInd),
            None, None,
            None, None, None)

      case tr: TradeReportRaw =>

          Event(None, EventType.Transaction,
            tr.messageSequenceNumber, tr.timeStamp,
            tr.tiCode, tr.marketSegmentCode, tr.currencyCode,
            None, None, None, None,
            Some(tr.tradeSize), Some(tr.broadcastUpdateAction),
            None, None, tr.tradePrice, None,
            None, None,
            tr.tradeCode, Some(tr.tradeTimeInd), Some(tr.convertedPriceInd))
    }

  }

}
