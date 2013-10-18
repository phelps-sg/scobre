package org.ccfea.tickdata.sql

import org.ccfea.tickdata.rawdata._
import scala.slick.driver.MySQLDriver.simple._

/**
 * Sql tables containing the imported LSE data in the original (raw) format.
 *
 * (c) Steve Phelps 2013
 */
object RawTables {

  val orderDetails = new Table[OrderDetailRaw]("order_detail_raw") {

    def orderCode = column[String]("order_code")
    def marketSegmentCode = column[String]("market_segment_code")
    def marketSectorCode = column[String]("market_sector_code")
    def tiCode = column[String]("ti_code")
    def countryOfRegister = column[String]("country_of_register")
    def currencyCode = column[String]("currency_code")
    def participantCode = column[Option[String]]("participant_code")
    def buySellInd = column[String]("buy_sell_ind")
    def marketMechanismGroup = column[String]("market_mechanism_group")
    def marketMechanismType = column[String]("market_mechanism_type")
    def price = column[BigDecimal]("price")
    def aggregateSize = column[Long]("aggregate_size")
    def singleFillInd = column[String]("single_fill_ind")
    def broadcastUpdateAction = column[String]("broadcast_update_action")
    def date = column[String]("date")
    def time = column[String]("time")
    def messageSequenceNumber = column[Long]("message_sequence_number")

    def * = orderCode ~ marketSegmentCode ~ marketSectorCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ participantCode ~ buySellInd ~ marketMechanismGroup ~ marketMechanismType ~ price ~ aggregateSize ~ singleFillInd ~ broadcastUpdateAction ~ date ~ time ~ messageSequenceNumber <>(OrderDetailRaw, OrderDetailRaw.unapply _)
  }

  val orderHistory = new Table[OrderHistoryRaw]("order_history_raw") {

    def orderCode = column[String]("order_code")
    def matchingOrderCode = column[Option[String]]("matching_order_code", O.Nullable)
    def orderActionType = column[String]("order_action_type")
    def tradeSize = column[Long]("trade_size")
    def tradeCode = column[Option[String]]("trade_code", O.Nullable)
    def marketSegmentCode = column[String]("market_segment_code")
    def tiCode = column[String]("ti_code")
    def countryOfRegister = column[String]("country_of_register")
    def currencyCode = column[String]("currency_code")
    def buySellInd = column[String]("buy_sell_ind")
    def marketMechanismGroup = column[String]("market_mechanism_group")
    def marketMechanismType = column[String]("market_mechanism_type")
    def aggregateSize = column[Long]("aggregate_size")
    def date = column[String]("date")
    def time = column[String]("time")
    def messageSequenceNumber = column[Long]("message_sequence_number")

    def * = orderCode ~ orderActionType ~ matchingOrderCode ~ tradeSize ~ tradeCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ marketSegmentCode ~ aggregateSize ~ buySellInd ~ marketMechanismType ~ messageSequenceNumber ~ date ~ time <>(OrderHistoryRaw, OrderHistoryRaw.unapply _)
  }

  val tradeReports = new Table[TradeReportRaw]("trade_reports_raw") {

    def messageSequenceNumber = column[Long]("message_sequence_number")
    def tiCode = column[String]("ti_code")
    def marketSegmentCode = column[String]("market_segment_code")
    def countryOfRegister = column[String]("country_of_register")
    def currencyCode = column[String]("currency_code")
    def tradeCode = column[Option[String]]("trade_code")
    def tradePrice = column[BigDecimal]("trade_price")
    def tradeSize = column[Long]("trade_size")
    def date = column[String]("trade_date")
    def time = column[String]("trade_time")
    def broadcastUpdateAction = column[String]("broadcast_update_action")
    def tradeTypeInd = column[String]("trade_type_ind")
    def tradeTimeInd = column[String]("trade_time_ind")
    def bargainConditions = column[String]("bargain_conditions")
    def convertedPriceInd = column[String]("converted_price_ind")
    def publicationDate = column[String]("publication_date")
    def publicationTime = column[String]("publication_time")

    def * = messageSequenceNumber ~ tiCode ~ marketSegmentCode ~
      countryOfRegister ~ currencyCode ~ tradeCode ~ tradePrice.? ~
      tradeSize ~ date ~ time ~ broadcastUpdateAction ~
      tradeTypeInd ~ tradeTimeInd ~ bargainConditions ~
      convertedPriceInd ~ publicationDate ~ publicationTime <>
      (TradeReportRaw, TradeReportRaw.unapply _)
  }
}


