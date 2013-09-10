import net.sourceforge.jasa.market._

// import scala.slick.direct._
// import scala.slick.direct.AnnotationMapper._
// import scala.reflect.runtime.universe
// import scala.slick.jdbc.StaticQuery.interpolation

import scala.slick.driver.MySQLDriver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

import java.text.{DateFormat, SimpleDateFormat}

abstract class HasDateTime {
  val df = new SimpleDateFormat("ddMMyyyy hh:mm:ss")
  def date: String
  def time: String
  def timeStamp: Long = { 
    df.parse("%s %s".format(date, time)).getTime()
  }
}

object EventType extends Enumeration {
  val OrderSubmitted = Value(1)
  val OrderRevised = Value(2)
  val Transaction = Value(3)
}

object IdentifierCounter {
  var counter: Long = 0
  def next = {
    counter = counter + 1
    counter
  }
}

object ParseRawData {
	
	def main(args : Array[String]) {
	  	
		case class OrderDetail(orderCode: String, marketSegmentCode: String, 
								marketSectorCode: String, tiCode: String, 
								countryofRegister: String, currencyCode: String, 
								participantCode: Option[String], buySellInd: String, 
								marketMechanismGroup: String, 
								marketMechanismType: String, 
								price: Long, aggregateSize: Long, 
								singleFillInd: String, 
								broadcastUpdateAction: String, 
								date: String, 
								time: String,
								messageSequenceNumber: Long) extends HasDateTime
		val orderDetailsRaw = new Table[OrderDetail]("order_detail_raw") {
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
			def price = column[Long]("price")
			def aggregateSize = column[Long]("aggregate_size")
			def singleFillInd = column[String]("single_fill_ind")
			def broadcastUpdateAction = column[String]("broadcast_update_action")
			def date = column[String]("date")
			def time = column[String]("time")
			def messageSequenceNumber = column[Long]("message_sequence_number")
			def * = orderCode ~ marketSegmentCode ~ marketSectorCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ participantCode ~ buySellInd ~ marketMechanismGroup ~ marketMechanismType ~ price ~ aggregateSize ~ singleFillInd ~ broadcastUpdateAction ~ date ~ time ~ messageSequenceNumber <> (OrderDetail, OrderDetail.unapply _)
		}
			
		case class Order(orderCode: String, 
								marketSectorCode: String, 
								participantCode: Option[String], buySellInd: String, 
								marketMechanismGroup: String, 
								marketMechanismType: String, 
								price: Long, aggregateSize: Long, 
								singleFillInd: String, 
								broadcastUpdateAction: String)
		val orders = new Table[Order]("orders") {
			def orderCode = column[String]("order_code")
			def marketSectorCode = column[String]("market_sector_code")
			def participantCode = column[Option[String]]("participant_code")
			def buySellInd = column[String]("buy_sell_ind")
			def marketMechanismGroup = column[String]("market_mechanism_group")
			def marketMechanismType = column[String]("market_mechanism_type")
			def price = column[Long]("price")
			def aggregateSize = column[Long]("aggregate_size")
			def singleFillInd = column[String]("single_fill_ind")
			def broadcastUpdateAction = column[String]("broadcast_update_action")
			def * = orderCode ~ marketSectorCode ~ participantCode ~ buySellInd ~ marketMechanismGroup ~ marketMechanismType ~ price ~ aggregateSize ~ singleFillInd ~ broadcastUpdateAction <> (Order, Order.unapply _)
		}

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
		val orderHistoryRaw = new Table[OrderHistoryRaw]("order_history_raw") {
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
			def * = orderCode ~ orderActionType ~ matchingOrderCode ~ tradeSize ~ tradeCode ~  tiCode ~ countryOfRegister ~ currencyCode ~ marketSegmentCode ~ aggregateSize ~ buySellInd ~ marketMechanismType ~ messageSequenceNumber ~ date ~ time  <> (OrderHistoryRaw, OrderHistoryRaw.unapply _)
		}

		case class OrderHistory(eventID: Option[Long],
										orderCode: String,
										orderActionType: String,
										matchingOrderCode: Option[String],
										tradeSize: Long,
										tradeCode: Option[String],
										aggregateSize: Long,
										buySellInd: String, 
										marketMechanismType: String)
		val orderHistory = new Table[OrderHistory]("order_history") {
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
			def * = eventID.? ~ orderCode ~ orderActionType ~ matchingOrderCode ~ tradeSize ~ tradeCode ~  aggregateSize ~ buySellInd ~ marketMechanismType <> (OrderHistory, OrderHistory.unapply _)
		}
		
		implicit val eventTypeMapper =
				MappedTypeMapper.base[EventType.Value, Int] (
				    {
				      ev => ev.id
				    },
				    {
				      id => EventType(id)
				    })
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
					def * = eventID ~ eventType ~ orderHistoryEventID ~ orderCode ~ transactionID ~ messageSequenceNumber ~ timeStamp ~ tiCode ~ marketSegmentCode ~ countryOfRegister ~ currencyCode <> (Event, Event.unapply _)
		}


//		val df = new SimpleDateFormat("ddMMyyyy hh:mm:ss.S")
		
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
		    			Order(orderCode, marketSectorCode, participantCode, 
		    					buySellInd, marketMechanismGroup, 
		    					marketMechanismType, price, aggregateSize, 
		    					singleFillInd, broadcastUpdateAction),
		    					
		    			Event(None, EventType.OrderSubmitted, None, Some(orderCode), 
		    						None, messageSequenceNumber, event.timeStamp, 
		    						tiCode, marketSegmentCode, countryOfRegister, currencyCode)
		    	)
		    	
		  }
		  
		  
		}
		
		def parseAndInsert(batchSize: Int = 1000, rawQuery: Query[Any,_ <:HasDateTime],
								objectInserter: Seq[Any] => Option[Int]) = {
			var finished = false
			var offset = 0
			do {
				val shortQuery = rawQuery.drop(offset).take(batchSize)
//				finished = shortQuery.list.length < batchSize
				val parsed = shortQuery.list.map(parseEvent(_, IdentifierCounter.next))
				println(parsed)
				val numRows: Int = objectInserter(parsed.map(x => x._1)) match {
				  case Some(x: Int) => x 
				  case _ => 
				    throw 
				    	new UnsupportedOperationException("Unsupported database")
				}
				events.insertAll(parsed.map(x => x._2): _*)
				offset = offset + numRows
			} while (!finished)	  
		}

		Database.forURL("jdbc:mysql://cseesp1/lse_tickdata?user=sphelps&password=th0rnxtc", 
				driver="com.mysql.jdbc.Driver") withSession {

//			parseAndInsert(rawQuery = Query(orderDetailsRaw), objectInserter =
//			  (objects: Seq[Any]) =>
//			    		orders.insertAll(objects.map( (x: Any) => x match {
//			    		  case x: Order => x
//			    		}): _*)
//			)
			    		

			parseAndInsert(rawQuery = Query(orderHistoryRaw), objectInserter = 
			  (objects: Seq[Any]) => 
			    		orderHistory.insertAll(objects.map( (x: Any) => x match {
			    							case x: OrderHistory => x
			    							}): _*)
			)

		}
	}
}
