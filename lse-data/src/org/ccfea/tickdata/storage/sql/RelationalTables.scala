package org.ccfea.tickdata.storage.sql

import scala.slick.driver.MySQLDriver.simple._
import org.ccfea.tickdata.event.{EventType, Event}
import org.ccfea.tickdata.order.TradeDirection

/**
 * SQL Schema for parsed events data.
 *
 * (c) Steve Phelps 2013
 */

object RelationalTables {

  implicit val eventTypeMapper =
    MappedTypeMapper.base[EventType.Value, String](
    {
      ev => ev.toString
    }, {
      id => id match {
        case "transaction"       => EventType.Transaction
        case "order_submitted"   => EventType.OrderSubmitted
        case "order_revised"     => EventType.OrderRevised
        case "order_deleted"     => EventType.OrderDeleted
        case "order_expired"     => EventType.OrderExpired
        case "order_matched"     => EventType.OrderMatched
        case "order_filled"      => EventType.OrderFilled
        case "transaction_limit" => EventType.TransactionLimit
      }
    })

  implicit val tradeDirectionMapper =
    MappedTypeMapper.base[TradeDirection.Value, String](
    {
      td => td.toString
    }, {
      id => id match {
        case "buy"  => TradeDirection.Buy
        case "sell" => TradeDirection.Sell
      }
    })

  val events = new Table[Event]("events") {

    def eventID = column[Option[Long]]("event_id", O.AutoInc, O.PrimaryKey)

    def eventType = column[EventType.Value]("event_type")
    def orderCode = column[Option[String]]("order_code")
    def messageSequenceNumber = column[Long]("message_sequence_number")
    def timeStamp = column[Long]("time_stamp")
    def tiCode = column[String]("ti_code")
    def marketSegmentCode = column[String]("market_segment_code")
    def countryOfRegister = column[String]("country_of_register")
    def currencyCode = column[String]("currency_code")
    def marketSectorCode = column[Option[String]]("market_sector_code")
    def participantCode = column[Option[String]]("participant_code")
    def tradeDirection = column[Option[TradeDirection.Value]]("trade_direction")
    def marketMechanismGroup = column[Option[String]]("market_mechanism_group")
    def marketMechanismType = column[Option[String]]("market_mechanism_type")
    def price = column[Option[BigDecimal]]("price")
    def aggregateSize = column[Option[Long]]("aggregate_size")
    def singleFillInd = column[Option[String]]("single_fill_ind")
    def broadcastUpdateAction = column[Option[String]]("broadcast_update_action")
    def matchingOrderCode = column[Option[String]]("matching_order_code", O.Nullable)
    def orderActionType = column[Option[String]]("order_action_type")
    def tradeSize = column[Option[Long]]("trade_size")
    def resultingTradeCode = column[Option[String]]("resulting_trade_code", O.Nullable)
    def tradeCode = column[Option[String]]("trade_code")
    //      def tradePrice = column[Option[BigDecimal]]("trade_price")
    def tradeTypeInd = column[Option[String]]("trade_type_ind")
    def tradeTimeInd = column[Option[String]]("trade_time_ind")
    def bargainConditions = column[Option[String]]("bargain_conditions")
    def convertedPriceInd = column[Option[String]]("converted_price_ind")
    //      def publicationTimeStamp = column[Option[Long]]("publication_time_stamp")

    def * = eventID ~ eventType ~ messageSequenceNumber ~ timeStamp ~ tiCode ~ marketSegmentCode ~ currencyCode ~ marketMechanismType ~ aggregateSize ~ tradeDirection ~ orderCode ~ tradeSize ~ broadcastUpdateAction ~ marketSectorCode ~ marketMechanismGroup ~ price ~ singleFillInd ~ matchingOrderCode ~ resultingTradeCode ~ tradeCode ~ tradeTimeInd ~ convertedPriceInd <> (Event, Event.unapply _)
  }

}

