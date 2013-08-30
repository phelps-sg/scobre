import net.sourceforge.jasa.market._

// import scala.slick.direct._
// import scala.slick.direct.AnnotationMapper._
// import scala.reflect.runtime.universe
// import scala.slick.jdbc.StaticQuery.interpolation

import scala.slick.driver.MySQLDriver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

//import collection.JavaConversions._

object Test {
	
	def main(args : Array[String]) {
		val order = new Order()
		order.setPrice(100.0)
		order.setQuantity(20)
		order.setIsBid(true)
		val book = new FourHeapOrderBook()
		book.insertUnmatchedBid(order)
		println(book)
		
		val OrderDetails = new Table[(String, String, String, String, String, String, String, String, String, Long, Long, String, String, String, String, Long)]("tblOrderDetail") {
			def orderCode = column[String]("OrderCode");
			def marketSegmentCode = column[String]("MarketSegmentCode");
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
			def broadcastUpdateAction = column[String]("SingleFillInd");
			def date = column[String]("Date");
			def time = column[String]("Time");
			def messageSequenceNumber = column[Long]("MessageSequenceNumber");
			def * = orderCode ~ marketSegmentCode ~ tiCode ~ countryOfRegister ~ currencyCode ~ participantCode ~ buySellInd ~ marketMechanismGroup ~ marketMechanismType ~ price ~ aggregateSize ~ singleFillInd ~ broadcastUpdateAction ~ date ~ time ~ messageSequenceNumber
		}
		
		Database.forURL("jdbc:mysql://localhost/lse?user=root&password=th0rnxtc", driver="com.mysql.jdbc.Driver") withSession {
			val priceQuery = for(o <- Query(OrderDetails)) yield o.date ~ o.time
			println(priceQuery)
 			for(p <- priceQuery) println(p);
//			println(priceQuery.first)
		}
	}
}
