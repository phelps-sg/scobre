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
				event <- events
			    transaction <- event.transaction
			} yield (event.timeStamp, transaction.tradePrice, event.eventType)
			
			val allOrdersQuery = for {
			  event <- events 
			  order <- event.order
			} yield (event.timeStamp, order.price, event.eventType)
			
			val allRevisionsQuery = for {
			  event <- events 
			  orderHistory <- event.orderHistory 
			} yield (event.timeStamp, BigDecimal(0), event.eventType)
			
			val allEventsQuery = 
			  (allTransactionsQuery union allOrdersQuery union 
			      allRevisionsQuery).sortBy(_._1)
			
			println(allEventsQuery.selectStatement)
			for((t, price, eventType) <- allEventsQuery) {
			  println(new java.util.Date(t) + ": " + price + " (" + eventType + ")")
			}
			
		}

  }
}
