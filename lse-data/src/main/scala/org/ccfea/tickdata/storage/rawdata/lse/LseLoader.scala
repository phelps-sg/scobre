package org.ccfea.tickdata.storage.rawdata.lse

import grizzled.slf4j.Logger
import org.ccfea.tickdata.event.{EventType, Event}
import org.ccfea.tickdata.order.{TradeDirection, MarketMechanismType}
import org.ccfea.tickdata.storage.DataLoader
import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * (C) Steve Phelps 2014
 */
trait LseLoader extends DataLoader {

  override val logger = Logger("org.ccfea.tickdata.storage.rawdata.lse.LseLoader")

  /**
   * One of "order_history_raw", "order_detail_raw" or "trade_reports_raw" to indicate the
   * source of the data.
   */
  val recordType: String

  def toRecord(values: Array[Option[String]], lineNumber: Long): HasDateTime = {

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

      case OrderHistoryRaw(orderCode, orderActionType, matchingOrderCode,
                              tradeSize, tradeCode, tiCode, countryOfRegister,
                              currencyCode, marketSegmentCode,
                              aggregateSize, buySellInd,
                              marketMechanismType,
                              messageSequenceNumber, date, time) =>

          Event(None, orderActionType, messageSequenceNumber,
            rawEvent.timeStamp, tiCode, marketSegmentCode, currencyCode,
            Some(marketMechanismType), Some(aggregateSize), Some(buySellInd),
            Some(orderCode), tradeSize, None, None, None, None, None,
            matchingOrderCode, tradeCode,
            None, None, None)


      case OrderDetailRaw(orderCode, marketSegmentCode, marketSectorCode,
                          tiCode, countryOfRegister, currencyCode,
                          participantCode, buySellInd, marketMechanismGroup,
                          marketMechanismType, price, aggregateSize,
                          singleFillInd, broadcastUpdateAction, date, time,
                          messageSequenceNumber) =>

          Event(None, EventType.OrderSubmitted, messageSequenceNumber,
            rawEvent.timeStamp, tiCode, marketSegmentCode, currencyCode,
            Some(marketMechanismType), Some(aggregateSize), Some(buySellInd),
            Some(orderCode), None, Some(broadcastUpdateAction),
            Some(marketSectorCode), Some(marketMechanismGroup), Some(price), Some(singleFillInd),
            None, None,
            None, None, None)

      case TradeReportRaw(messageSequenceNumber, tiCode, marketSegmentCode,
                          countryOfRegister, currencyCode, tradeCode,
                          tradePrice, tradeSize, date, time,
                          broadcastUpdateAction, tradeTypeInd,
                          tradeTimeInd, bargainConditions, convertedPriceInd,
                          publicationDate, publicationTime) =>

          Event(None, EventType.Transaction,
            messageSequenceNumber, rawEvent.timeStamp,
            tiCode, marketSegmentCode, currencyCode,
            None, None, None, None,
            Some(tradeSize), Some(broadcastUpdateAction),
            None, None, tradePrice, None,
            None, None,
            tradeCode, Some(tradeTimeInd), Some(convertedPriceInd))
    }

  }

}
