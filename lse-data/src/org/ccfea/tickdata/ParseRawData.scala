package org.ccfea.tickdata

import net.sourceforge.jasa.market._
import scala.slick.driver.MySQLDriver.simple._
import java.text.SimpleDateFormat

// Use the implicit threadLocalSession

import Database.threadLocalSession

object TickDatabase {

  val database = "lse_tickdata"

  def url(args: Array[String]) = {

    val host = args(0)
    val user = args(1)
    val password = args(2)
    val port = if (args.length < 4) "3306" else args(3)

    "jdbc:mysql://%s:%s/%s?user=%s&password=%s".format(
      host, port, database, user, password)
  }

}

trait HasDateTime {
  val df = new SimpleDateFormat("ddMMyyyy hh:mm:ss")

  def date: String

  def time: String

  def timeStamp: Long = {
    df.parse("%s %s".format(date, time)).getTime()
  }
}

case class DateFormatter(date: String, time: String) extends HasDateTime

object EventType extends Enumeration {
  val None = Value("")
  val OrderSubmitted = Value("order_submitted")
  val OrderRevised = Value("order_revised")
  val Transaction = Value("transaction")
}

object IdentifierCounter {
  var counter: Long = 0

  def next = {
    counter = counter + 1
    counter
  }
}

case class Order(orderCode: Option[String],
                 marketSectorCode: String,
                 participantCode: Option[String], buySellInd: String,
                 marketMechanismGroup: String,
                 marketMechanismType: String,
                 price: BigDecimal, aggregateSize: Long,
                 singleFillInd: String,
                 broadcastUpdateAction: String)

case class OrderHistory(eventID: Option[Long],
                        orderCode: String,
                        orderActionType: String,
                        matchingOrderCode: Option[String],
                        tradeSize: Long,
                        tradeCode: Option[String],
                        aggregateSize: Long,
                        buySellInd: String,
                        marketMechanismType: String)

case class Event(eventID: Option[Long],
                 eventType: EventType.Value,
                 orderHistoryEventID: Option[Long],
                 orderCode: Option[String],
                 transactionID: Option[Long],
                 messageSequenceNumber: Long,
                 timeStamp: Long,
                 tiCode: String,
                 marketSegmentCode: String,
                 countryOfRegister: String,
                 currencyCode: String)

case class Transaction(transactionID: Option[Long],
                       tradeCode: Option[String],
                       tradePrice: Option[BigDecimal],
                       tradeSize: Option[Long],
                       broadcastUpdateAction: Option[String],
                       tradeTypeInd: Option[String],
                       tradeTimeInd: Option[String],
                       bargainConditions: Option[String],
                       convertedPriceInd: Option[String],
                       publicationTimeStamp: Option[Long])

case class OrderDetail(orderCode: String, marketSegmentCode: String,
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

  val orderDetails = new Table[OrderDetail]("order_detail_raw") {

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

    def * = orderCode ~ marketSegmentCode ~ marketSectorCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ participantCode ~ buySellInd ~ marketMechanismGroup ~ marketMechanismType ~ price ~ aggregateSize ~ singleFillInd ~ broadcastUpdateAction ~ date ~ time ~ messageSequenceNumber <>(OrderDetail, OrderDetail.unapply _)
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

  val orders = new Table[Order]("orders") {
    def orderCode = column[Option[String]]("order_code", O.AutoInc, O.PrimaryKey)
    def marketSectorCode = column[String]("market_sector_code")
    def participantCode = column[Option[String]]("participant_code")
    def buySellInd = column[String]("buy_sell_ind")
    def marketMechanismGroup = column[String]("market_mechanism_group")
    def marketMechanismType = column[String]("market_mechanism_type")
    def price = column[BigDecimal]("price")
    def aggregateSize = column[Long]("aggregate_size")
    def singleFillInd = column[String]("single_fill_ind")
    def broadcastUpdateAction = column[String]("broadcast_update_action")

    def * = orderCode ~ marketSectorCode ~ participantCode ~ buySellInd ~ marketMechanismGroup ~ marketMechanismType ~ price ~ aggregateSize ~ singleFillInd ~ broadcastUpdateAction <>(Order, Order.unapply _)
  }

  val orderHistories = new Table[OrderHistory]("order_history") {

    def eventID = column[Long]("event_id", O.AutoInc, O.PrimaryKey);
    def orderCode = column[String]("order_code")
    def matchingOrderCode = column[Option[String]]("matching_order_code", O.Nullable)
    def orderActionType = column[String]("order_action_type")
    def tradeSize = column[Long]("trade_size")
    def tradeCode = column[Option[String]]("trade_code", O.Nullable)
    def buySellInd = column[String]("buy_sell_ind")
    def marketMechanismGroup = column[String]("market_mechanism_group")
    def marketMechanismType = column[String]("market_mechanism_type")
    def aggregateSize = column[Long]("aggregate_size")

    def * = eventID.? ~ orderCode ~ orderActionType ~ matchingOrderCode ~ tradeSize ~ tradeCode ~ aggregateSize ~ buySellInd ~ marketMechanismType <>(OrderHistory, OrderHistory.unapply _)
  }

  val transactions = new Table[Transaction]("transactions") {

    def transactionID = column[Option[Long]]("transaction_id", O.AutoInc, O.PrimaryKey)
    def tradeCode = column[String]("trade_code")
    def tradePrice = column[BigDecimal]("trade_price")
    def tradeSize = column[Long]("trade_size")
    def broadcastUpdateAction = column[String]("broadcast_update_action")
    def tradeTypeInd = column[String]("trade_type_ind")
    def tradeTimeInd = column[String]("trade_time_ind")
    def bargainConditions = column[String]("bargain_conditions")
    def convertedPriceInd = column[String]("converted_price_ind")
    def publicationTimeStamp = column[Long]("publication_time_stamp")

    def * = transactionID ~ tradeCode.? ~ tradePrice.? ~ tradeSize.? ~
      broadcastUpdateAction.? ~ tradeTypeInd.? ~ tradeTimeInd.? ~
      bargainConditions.? ~ convertedPriceInd.? ~
      publicationTimeStamp.? <>(Transaction, Transaction.unapply _)
  }

  implicit val eventTypeMapper =
    MappedTypeMapper.base[EventType.Value, String](
    {
      ev => ev.toString
    }, {
      id => id match {
        case "transaction" => EventType.Transaction
        case "order_submitted" => EventType.OrderSubmitted
        case "order_revised" => EventType.OrderRevised
      }
    })
  val events = new Table[Event]("events") {

    def eventID = column[Option[Long]]("event_id", O.AutoInc, O.PrimaryKey)
    def eventType = column[EventType.Value]("event_type")
    def orderHistoryEventID = column[Option[Long]]("order_history_event_id")
    def orderCode = column[Option[String]]("order_code")
    def transactionID = column[Option[Long]]("transaction_id")
    def messageSequenceNumber = column[Long]("message_sequence_number")
    def timeStamp = column[Long]("time_stamp")
    def tiCode = column[String]("ti_code")
    def marketSegmentCode = column[String]("market_segment_code")
    def countryOfRegister = column[String]("country_of_register")
    def currencyCode = column[String]("currency_code")

    def * = eventID ~ eventType ~ orderHistoryEventID ~ orderCode ~ transactionID ~ messageSequenceNumber ~ timeStamp ~ tiCode ~ marketSegmentCode ~ countryOfRegister ~ currencyCode <>(Event, Event.unapply _)

    def transaction = foreignKey("transaction_fk", transactionID, transactions)(_.transactionID)
    def order = foreignKey("order_fk", orderCode, orders)(_.orderCode)
    def orderHistory = foreignKey("order_fk", orderHistoryEventID, orderHistories)(_.eventID)
  }

}

import RelationalTables._

object ParseRawData {

  def parseEvent(event: HasDateTime, eventID: => Long) = {

    val id: Long = eventID

    event match {

      case OrderHistoryRaw(orderCode, orderActionType, matchingOrderCode,
                          tradeSize, tradeCode, tiCode, countryOfRegister,
                          currencyCode, marketSegmentCode,
                          aggregateSize, buySellInd,
                          marketMechanismType,
                          messageSequenceNumber, date, time) =>

              (

                OrderHistory(Some(id), orderCode, orderActionType,
                  matchingOrderCode, tradeSize, tradeCode,
                  aggregateSize, buySellInd, marketMechanismType),

                Event(None, EventType.OrderRevised, Some(id), Some(orderCode),
                  None, messageSequenceNumber, event.timeStamp,
                  tiCode, marketSegmentCode, countryOfRegister, currencyCode)

                )

      case OrderDetail(orderCode, marketSegmentCode, marketSectorCode,
                        tiCode, countryOfRegister, currencyCode,
                        participantCode, buySellInd, marketMechanismGroup,
                        marketMechanismType, price, aggregateSize,
                        singleFillInd, broadcastUpdateAction, date, time,
                        messageSequenceNumber) =>

              (
                Order(Some(orderCode), marketSectorCode, participantCode,
                  buySellInd, marketMechanismGroup,
                  marketMechanismType, price, aggregateSize,
                  singleFillInd, broadcastUpdateAction),

                Event(None, EventType.OrderSubmitted, None, Some(orderCode),
                  None, messageSequenceNumber, event.timeStamp,
                  tiCode, marketSegmentCode, countryOfRegister, currencyCode)
                )


      case TradeReportRaw(messageSequenceNumber, tiCode, marketSegmentCode,
                          countryOfRegister, currencyCode, tradeCode,
                          tradePrice, tradeSize, date, time,
                          broadcastUpdateAction, tradeTypeInd,
                          tradeTimeInd, bargainConditions, convertedPriceInd,
                          publicationDate, publicationTime) =>

              (
                Transaction(Some(id), tradeCode, tradePrice, Some(tradeSize),
                  Some(broadcastUpdateAction), Some(tradeTypeInd),
                  Some(tradeTimeInd), Some(bargainConditions),
                  Some(convertedPriceInd),
                  Some(
                    DateFormatter(publicationDate, publicationTime).
                      timeStamp)),

                Event(None, EventType.Transaction, None, None, Some(id),
                  messageSequenceNumber, event.timeStamp,
                  tiCode, marketSegmentCode, countryOfRegister,
                  currencyCode)
                )

    }


  }

  def parseAndInsert(batchSize: Int = 2000, rawQuery: Query[Any, _ <: HasDateTime],
                     objectInserter: Seq[Any] => Option[Int]) = {
    println(rawQuery.selectStatement)
    var finished = false
    var offset = 0
    do {
      val shortQuery = rawQuery.drop(offset).take(batchSize)
      finished = shortQuery.list.length < batchSize
      val parsed = shortQuery.list.map(parseEvent(_, IdentifierCounter.next))
      val numRows: Int = objectInserter(parsed.map(x => x._1)) match {
        case Some(x: Int) => x
        case _ =>
          throw
            new UnsupportedOperationException("Unsupported database")
      }
      events.insertAll(parsed.map(x => x._2): _*)
      offset = offset + numRows
    } while (!finished)
    println("done.")
  }

  def main(args: Array[String]) {

    val url = TickDatabase.url(args)
    Database.forURL(url, driver = "com.mysql.jdbc.Driver") withSession {

      parseAndInsert(rawQuery = Query(RawTables.orderDetails), objectInserter =
        (objects: Seq[Any]) =>
          orders.insertAll(objects.map((x: Any) => x match {
            case x: Order => x
          }): _*)
      )

      parseAndInsert(rawQuery = Query(RawTables.orderHistory), objectInserter =
        (objects: Seq[Any]) =>
          orderHistories.insertAll(objects.map((x: Any) => x match {
            case x: OrderHistory => x
          }): _*)
      )

      parseAndInsert(rawQuery = Query(RawTables.tradeReports), objectInserter =
        (objects: Seq[Any]) =>
          transactions.insertAll(objects.map((x: Any) => x match {
            case x: Transaction => x
          }): _*)
      )

    }
  }
}
