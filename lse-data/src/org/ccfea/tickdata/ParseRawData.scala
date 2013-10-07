package org.ccfea.tickdata

import org.rogach.scallop._
import scala.slick.driver.MySQLDriver.simple._
import java.text.SimpleDateFormat

// Use the implicit threadLocalSession

import Database.threadLocalSession

class DbConf(args: Seq[String]) extends ScallopConf(args) {
  val url = trailArg[String](required = true)
  val driver = opt[String](default = Some("com.mysql.jdbc.Driver"))
}

class ParseConf(args: Seq[String]) extends DbConf(args) {
  val bufferSize = opt[Int](default = Some(2000))
}

trait HasDateTime {

  // without milliseconds
  val dfShort = new SimpleDateFormat("ddMMyyyy HH:mm:ss")

  // Time stamp format with milliseconds
  val df = new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS")

  def date: String
  def time: String

  def timeStamp: Long = {
    val dateTime = date + " " + time;
    if (time.length > 8 && time.contains(".")) {
      df.parse(dateTime).getTime()
    } else {
      dfShort.parse(dateTime).getTime()
    }
  }
}

case class DateFormatter(date: String, time: String) extends HasDateTime

object EventType extends Enumeration {
  val None = Value("")
  val OrderSubmitted = Value("order_submitted")
  val OrderRevised = Value("order_revised")
  val Transaction = Value("transaction")
  val OrderDeleted = Value("order_deleted")
  val OrderExpired = Value("order_expired")
  val OrderMatched = Value("order_matched")
  val OrderFilled = Value("order_filled")
  val TransactionLimit = Value("transaction_limit")
}

//
//case class Order(orderCode: Option[String],
//                 marketSectorCode: String,
//                 participantCode: Option[String], buySellInd: String,
//                 marketMechanismGroup: String,
//                 marketMechanismType: String,
//                 price: BigDecimal, aggregateSize: Long,
//                 singleFillInd: String,
//                 broadcastUpdateAction: String)
//
//case class OrderHistory(eventID: Option[Long],
//                        orderCode: String,
//                        orderActionType: String,
//                        matchingOrderCode: Option[String],
//                        tradeSize: Long,
//                        tradeCode: Option[String],
//                        aggregateSize: Long,
//                        buySellInd: String,
//                        marketMechanismType: String)

case class Event(eventID: Option[Long],

                 eventType: EventType.Value,

                 messageSequenceNumber: Long,
                 timeStamp: Long,
                 tiCode: String,
                 marketSegmentCode: String,
//                 countryOfRegister: String,
//                 currencyCode: String,

                 marketMechanismType: Option[String],
                 aggregateSize: Option[Long],
                 buySellInd: Option[String],
                 orderCode: Option[String],

                 tradeSize: Option[Long],
                 broadcastUpdateAction: Option[String],

                 marketSectorCode: Option[String],
//                 participantCode: Option[String],
                 marketMechanismGroup: Option[String],
                 price: Option[BigDecimal],
                 singleFillInd: Option[String],

//                 orderActionType: Option[String],
                 matchingOrderCode: Option[String],
                 resultingTradeCode: Option[String],

                 tradeCode: Option[String],
//                 tradePrice: Option[BigDecimal],
//                 tradeTypeInd: Option[String],
                 tradeTimeInd: Option[String],
//                 bargainConditions: Option[String],
                 convertedPriceInd: Option[String]
//                 publicationTimeStamp: Option[Long]
)

//
//case class Transaction(transactionID: Option[Long],
//                       tradeCode: Option[String],
//                       tradePrice: Option[BigDecimal],
//                       tradeSize: Option[Long],
//                       broadcastUpdateAction: Option[String],
//                       tradeTypeInd: Option[String],
//                       tradeTimeInd: Option[String],
//                       bargainConditions: Option[String],
//                       convertedPriceInd: Option[String],
//                       publicationTimeStamp: Option[Long])
//
case class OrderDetailRaw(orderCode: String, marketSegmentCode: String,
                       marketSectorCode: String, tiCode: String,
                       countryofRegister: String, currencyCode: String,
                       participantCode: Option[String], buySellInd: String,
                       marketMechanismGroup: String,
                       marketMechanismType: String,
                       price: BigDecimal, aggregateSize: Long,
                       singleFillInd: String,
                       broadcastUpdateAction: String,
                       date: String,
                       time: String,
                       messageSequenceNumber: Long) extends HasDateTime

case class OrderHistoryRaw(orderCode: String,
                           orderActionType: String,
                           matchingOrderCode: Option[String],
                           tradeSize: Long,
                           tradeCode: Option[String],
                           tiCode: String,
                           countryofRegister: String,
                           currencyCode: String,
                           marketSegmentCode: String,
                           aggregateSize: Long,
                           buySellInd: String,
                           marketMechanismType: String,
                           messageSequenceNumber: Long,
                           date: String,
                           time: String) extends HasDateTime

case class TradeReportRaw(messageSequenceNumber: Long,
                          tiCode: String,
                          marketSegmentCode: String,
                          countryOfRegister: String,
                          currencyCode: String,
                          tradeCode: Option[String],
                          tradePrice: Option[BigDecimal],
                          tradeSize: Long,
                          date: String,
                          time: String,
                          broadcastUpdateAction: String,
                          tradeTypeInd: String,
                          tradeTimeInd: String,
                          bargainConditions: String,
                          convertedPriceInd: String,
                          publicationDate: String,
                          publicationTime: String) extends HasDateTime

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
      def buySellInd = column[Option[String]]("buy_sell_ind")
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

      def * = eventID ~ eventType ~ messageSequenceNumber ~ timeStamp ~ tiCode ~ marketSegmentCode ~ marketMechanismType ~ aggregateSize ~ buySellInd ~ orderCode ~ tradeSize ~ broadcastUpdateAction ~ marketSectorCode ~ marketMechanismGroup ~ price ~ singleFillInd ~ matchingOrderCode ~ resultingTradeCode ~ tradeCode ~ tradeTimeInd ~ convertedPriceInd <> (Event, Event.unapply _)
    }

}

import RelationalTables._


trait DataLoader {

  val batchSize: Int

  def run: Unit

  def parseEvent(rawEvent: HasDateTime): Event = {

    implicit def orderActionTypeToEventType(orderActionType: String): EventType.Value = {
      orderActionType match {
        case "D" => EventType.OrderDeleted
        case "E" => EventType.OrderExpired
        case "P" => EventType.OrderMatched
        case "M" => EventType.OrderFilled
        case "T" => EventType.TransactionLimit
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
                        rawEvent.timeStamp, tiCode, marketSegmentCode,
                        Some(marketMechanismType), Some(aggregateSize), Some(buySellInd),
                        Some(orderCode), Some(tradeSize), None, None, None, None, None,
                        matchingOrderCode, tradeCode,
                        None, None, None)


      case OrderDetailRaw(orderCode, marketSegmentCode, marketSectorCode,
                        tiCode, countryOfRegister, currencyCode,
                        participantCode, buySellInd, marketMechanismGroup,
                        marketMechanismType, price, aggregateSize,
                        singleFillInd, broadcastUpdateAction, date, time,
                        messageSequenceNumber) =>


                Event(None, EventType.OrderSubmitted, messageSequenceNumber,
                      rawEvent.timeStamp, tiCode, marketSegmentCode,
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
                  tiCode, marketSegmentCode,
                  None, None, None, None,
                  Some(tradeSize), Some(broadcastUpdateAction),
                  None, None, tradePrice, None,
                  None, None,
                  tradeCode, Some(tradeTimeInd), Some(convertedPriceInd))

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

  def insertData(parsedEvents: Seq[Event]): Int

}

class SqlLoader(val batchSize: Int = 2000, val url: String, val driver: String) extends DataLoader {

  def run {
    Database.forURL(url = url, driver = driver) withSession {
      parseAndInsertData(Query(RawTables.orderDetails))
      parseAndInsertData(Query(RawTables.orderHistory))
      parseAndInsertData(Query(RawTables.tradeReports))
    }
  }

  def insertData(parsedEvents: Seq[Event]): Int = {
    events.insertAll(parsedEvents.seq: _*) match {
      case Some(x: Int) => x
      case _ =>
        throw
          new UnsupportedOperationException("Unsupported database")
    }
  }

}

object ParseRawData {

  def main(args: Array[String]) {

    val conf = new ParseConf(args)
    val loader = new SqlLoader(conf.bufferSize(), conf.url(), conf.driver())
    loader.run

  }
}
