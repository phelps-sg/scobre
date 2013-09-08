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

object Test {
  
	def testBook() {
	 	val order = new Order()
		order.setPrice(100.0)
		order.setQuantity(20)
		order.setIsBid(true)
		val book = new FourHeapOrderBook()
		book.insertUnmatchedBid(order)
		println(book) 
	}
	
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
								messageSequenceNumber: Long)
		val orderDetailsRaw = new Table[OrderDetail]("order_detail_raw") {
			def orderCode = column[String]("OrderCode");
			def marketSegmentCode = column[String]("MarketSegmentCode");
			def marketSectorCode = column[String]("MarketSectorCode");
			def tiCode = column[String]("TICode");
			def countryOfRegister = column[String]("CountryOfRegister");
			def currencyCode = column[String]("currencyCode");
			def participantCode = column[String]("ParticipantCode");
			def buySellInd = column[String]("BuySellInd");
			def marketMechanismGroup = column[String]("MarketMechanismGroup");
			def marketMechanismType = column[String]("MarketMechanismType");
			def price = column[Long]("Price");
			def aggregateSize = column[Long]("AggregateSize");
			def singleFillInd = column[String]("SingleFillInd");
			def broadcastUpdateAction = column[String]("BroadcastUpdateAction");
			def date = column[String]("Date");
			def time = column[String]("Time");
			def messageSequenceNumber = column[Long]("MessageSequenceNumber");
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
			def orderCode = column[String]("order_code");
			def marketSegmentCode = column[String]("market_segment_code");
			def marketSectorCode = column[String]("market_sector_code");
			def tiCode = column[String]("ti_code");
			def countryOfRegister = column[String]("country_of_register");
			def currencyCode = column[String]("currency_code");
			def participantCode = column[String]("participant_code");
			def buySellInd = column[String]("buy_sell_ind");
			def marketMechanismGroup = column[String]("market_mechanism_group");
			def marketMechanismType = column[String]("market_mechanism_type");
			def price = column[Long]("price");
			def aggregateSize = column[Long]("aggregate_size");
			def singleFillInd = column[String]("single_fill_ind");
			def broadcastUpdateAction = column[String]("broadcast_update_action");
			def timeStamp = column[Long]("time_stamp");
			def messageSequenceNumber = column[Long]("message_sequence_number");
			def * = orderCode ~ marketSegmentCode ~ marketSectorCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ participantCode ~ buySellInd ~ marketMechanismGroup ~ marketMechanismType ~ price ~ aggregateSize ~ singleFillInd ~ broadcastUpdateAction ~ timeStamp ~ messageSequenceNumber  <> (Order, Order.unapply _)
		}
		
//		val df = new SimpleDateFormat("ddMMyyyy hh:mm:ss.S")
		val df = new SimpleDateFormat("ddMMyyyy hh:mm:ss")

		Database.forURL("jdbc:mysql://cseesp1/lse_tickdata?user=sphelps&password=th0rnxtc", 
				driver="com.mysql.jdbc.Driver") withSession {
			val orderDetailQuery = Query(orderDetailsRaw)
			
			val shortQuery = orderDetailQuery.take(100);
			val mapped = shortQuery.list.map( (x : OrderDetail) => {
 			  	val timeStamp = df.parse("%s %s".format(x.date, x.time)).getTime();
 			  	new Order(x.orderCode, x.marketSegmentCode, x.marketSectorCode, x.tiCode, 
				    x.countryofRegister, x.currencyCode, x.participantCode, 
				    	x.buySellInd, x.marketMechanismGroup, x.marketMechanismType, 
				    		x.price, x.aggregateSize, x.singleFillInd, 
				    			x.broadcastUpdateAction, timeStamp, 
				    				x.messageSequenceNumber);
			})
			orders.insertAll(mapped: _*)
		}
	}
}
