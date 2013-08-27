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
		
		val df = new SimpleDateFormat("ddMMyyyy hh:mm:ss.S")

		Database.forURL("jdbc:mysql://cseesp1/lse?user=sphelps&password=th0rnxtc", driver="com.mysql.jdbc.Driver") withSession {
			val priceQuery = for(o <- Query(OrderDetails)) yield o.date ~ o.time
			val shortPriceQuery = priceQuery.take(100)
 			for(p <- shortPriceQuery) {
 			  val timeStamp = df.parse("%s %s".format(p._1, p._2)).getTime();
 			  println(timeStamp);
 			}
		}
	}
}
