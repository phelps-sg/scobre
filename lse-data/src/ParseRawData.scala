import net.sourceforge.jasa.market._

// import scala.slick.direct._
// import scala.slick.direct.AnnotationMapper._
// import scala.reflect.runtime.universe
// import scala.slick.jdbc.StaticQuery.interpolation

import scala.slick.driver.MySQLDriver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

import java.text.{DateFormat, SimpleDateFormat}

//import collection.JavaConversions._

abstract class HasDateTime {
  val df = new SimpleDateFormat("ddMMyyyy hh:mm:ss")
  def date: String
  def time: String
  def timeStamp: Long = { 
    df.parse("%s %s".format(date, time)).getTime()
  }
}

object ParseRawData {
	
	def main(args : Array[String]) {
	  	
		case class OrderDetail(orderCode: String, marketSegmentCode: String, 
								marketSectorCode: String, tiCode: String, 
								countryofRegister: String, currencyCode: String, 
								participantCode: String, buySellInd: String, 
								marketMechanismGroup: String, 
								marketMechanismType: String, 
								price: Long, aggregateSize: Long, 
								singleFillInd: String, 
								broadcastUpdateAction: String, 
								date: String, 
								time: String,
								messageSequenceNumber: Long) extends HasDateTime
		val orderDetailsRaw = new Table[OrderDetail]("order_detail_raw") {
			def orderCode = column[String]("order-code")
			def marketSegmentCode = column[String]("market_segment_code")
			def marketSectorCode = column[String]("market_sector_code")
			def tiCode = column[String]("ti_code")
			def countryOfRegister = column[String]("country_of_register")
			def currencyCode = column[String]("currency_code")
			def participantCode = column[String]("participant_code")
			def buySellInd = column[String]("buy_sell_ind")
			def marketMechanismGroup = column[String]("market_mechanism_group")
			def marketMechanismType = column[String]("market_mechanism_type")
			def price = column[Long]("price")
			def aggregateSize = column[Long]("aggregate_size")
			def singleFillInd = column[String]("single_fill_ind")
			def broadcastUpdateAction = column[String]("broadcast_update_action")
			def date = column[String]("date")
			def time = column[String]("time")
			def messageSequenceNumber = column[Long]("message_sequence_number")
			def * = orderCode ~ marketSegmentCode ~ marketSectorCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ participantCode ~ buySellInd ~ marketMechanismGroup ~ marketMechanismType ~ price ~ aggregateSize ~ singleFillInd ~ broadcastUpdateAction ~ date ~ time ~ messageSequenceNumber <> (OrderDetail, OrderDetail.unapply _)
		}
			
		case class Order(orderCode: String, marketSegmentCode: String, 
								marketSectorCode: String, tiCode: String, 
									countryofRegister: String, currencyCode: String, 
								participantCode: String, buySellInd: String, 
								marketMechanismGroup: String, 
								marketMechanismType: String, 
								price: Long, aggregateSize: Long, 
								singleFillInd: String, 
								broadcastUpdateAction: String, 
								timeStamp: Long, 
								messageSequenceNumber: Long)
		val orders = new Table[Order]("orders") {
			def orderCode = column[String]("order_code")
			def marketSegmentCode = column[String]("market_segment_code")
			def marketSectorCode = column[String]("market_sector_code")
			def tiCode = column[String]("ti_code")
			def countryOfRegister = column[String]("country_of_register")
			def currencyCode = column[String]("currency_code")
			def participantCode = column[String]("participant_code")
			def buySellInd = column[String]("buy_sell_ind")
			def marketMechanismGroup = column[String]("market_mechanism_group")
			def marketMechanismType = column[String]("market_mechanism_type")
			def price = column[Long]("price")
			def aggregateSize = column[Long]("aggregate_size")
			def singleFillInd = column[String]("single_fill_ind")
			def broadcastUpdateAction = column[String]("broadcast_update_action")
			def timeStamp = column[Long]("time_stamp")
			def messageSequenceNumber = column[Long]("message_sequence_number")
			def * = orderCode ~ marketSegmentCode ~ marketSectorCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ participantCode ~ buySellInd ~ marketMechanismGroup ~ marketMechanismType ~ price ~ aggregateSize ~ singleFillInd ~ broadcastUpdateAction ~ timeStamp ~ messageSequenceNumber  <> (Order, Order.unapply _)
		}

		case class OrderHistoryRaw(orderCode: String,
										orderActionType: String,
										matchingOrderCode: String,
										tradeSize: Long,
										tradeCode: String,
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
		val orderHistoryRaw = new Table[OrderHistoryRaw]("order_history_raw") {
			def orderCode = column[String]("order_code")
			def matchingOrderCode = column[String]("matching_order_code")
			def orderActionType = column[String]("order_action_type")
			def tradeSize = column[Long]("trade_size")
			def tradeCode = column[String]("trade_code")
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
			def * = orderCode ~ orderActionType ~ matchingOrderCode ~ tradeSize ~ tradeCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ marketSegmentCode ~ aggregateSize ~ buySellInd ~ marketMechanismType ~ messageSequenceNumber ~ date ~ time  <> (OrderHistoryRaw, OrderHistoryRaw.unapply _)
		}

		case class OrderHistory(eventID: Option[Long],
										orderCode: String,
										orderActionType: String,
										matchingOrderCode: String,
										tradeSize: Long,
										tradeCode: String,
										tiCode: String, 
										countryofRegister: String, 
										currencyCode: String, 
										marketSegmentCode: String,
										aggregateSize: Long,
										buySellInd: String, 
										marketMechanismType: String, 
										timeStamp: Long,
										messageSequenceNumber: Long)
		val orderHistory = new Table[OrderHistory]("order_history") {
		    def eventID = column[Long]("event_id", O.AutoInc, O.PrimaryKey);
			def orderCode = column[String]("order_code")
			def matchingOrderCode = column[String]("matching_order_code")
			def orderActionType = column[String]("order_action_type")
			def tradeSize = column[Long]("trade_size")
			def tradeCode = column[String]("trade_code")
			def marketSegmentCode = column[String]("market_segment_code")
			def tiCode = column[String]("ti_code")
			def countryOfRegister = column[String]("country_of_register")
			def currencyCode = column[String]("currency_code")
			def buySellInd = column[String]("buy_sell_ind")
			def marketMechanismGroup = column[String]("market_mechanism_group")
			def marketMechanismType = column[String]("market_mechanism_type")
			def aggregateSize = column[Long]("aggregate_size")
			def timeStamp = column[Long]("time_stamp")
			def messageSequenceNumber = column[Long]("message_sequence_number")
			def * = eventID.? ~ orderCode ~ orderActionType ~ matchingOrderCode ~ tradeSize ~ tradeCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ marketSegmentCode ~ aggregateSize ~ buySellInd ~ marketMechanismType ~ timeStamp ~ messageSequenceNumber  <> (OrderHistory, OrderHistory.unapply _)
		}

//		val df = new SimpleDateFormat("ddMMyyyy hh:mm:ss.S")
		
		def parse(x : OrderHistoryRaw) = {
			new OrderHistory(None, x.orderCode, x.orderActionType, x.matchingOrderCode, x.tradeSize, x.tradeCode, x.tiCode, x.countryofRegister, x.currencyCode, x.marketSegmentCode, x.aggregateSize, x.buySellInd, x.marketMechanismType, x.timeStamp, x.messageSequenceNumber)
		}

		Database.forURL("jdbc:mysql://cseesp1/lse_tickdata?user=sphelps&password=th0rnxtc", 
				driver="com.mysql.jdbc.Driver") withSession {
			val batchSize = 1000
			var finished = false
			var offset = 0
			do {
				val shortQuery = 
				  Query(orderHistoryRaw).drop(offset).take(batchSize)
				finished = shortQuery.list.length < batchSize
				val mapped = shortQuery.list.map(parse)
				orderHistory.insertAll(mapped: _*) match {
				  case Some(x: Int) => offset = offset + x 
				  case _ => 
				    throw 
				    	new UnsupportedOperationException("Unsupported database")
				}
			} while (!finished)
		}
	}
}
