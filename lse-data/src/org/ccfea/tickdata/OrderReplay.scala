package org.ccfea.tickdata

import net.sourceforge.jasa.market._
import scala.slick.driver.MySQLDriver.simple._
import java.text.SimpleDateFormat
import RelationalTables._

// Use the implicit threadLocalSession
import Database.threadLocalSession

 object OrderReplay {


  def main(args: Array[String]) {

		val url = TickDatabase.url(args)
		Database.forURL(url, driver="com.mysql.jdbc.Driver") withSession {
	  
			val allTransactionsQuery = for {
				ev <- events //sortBy(_.timeStamp)
			    tr <- transactions if ev.transactionID === tr.transactionID
			} yield (ev.timeStamp, tr.tradePrice, ev.eventType)
			
			val allOrdersQuery = for {
			  ev <- events //sortBy(_.timeStamp)
			  order <- orders if ev.orderCode === orders.orderCode
			} yield (ev.timeStamp, order.price, ev.eventType)
			
			val allRevisionsQuery = for {
			  ev <- events 
			  orderHistory <- orderHistory if ev.orderHistoryEventID === orderHistory.eventID
			} yield (ev.timeStamp, BigDecimal(0), ev.eventType)
			
			val allEventsQuery = (allTransactionsQuery union allRevisionsQuery).sortBy(_._1)
			
			//println(allEventsQuery.selectStatement)
			
			for((t, price, eventType) <- allEventsQuery) {
			  println(new java.util.Date(t) + ": " + price + " (" + eventType + ")")
			}
			
		}

  }
}
