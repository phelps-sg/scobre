package org.ccfea.tickdata

import net.sourceforge.jasa.market._
import net.sourceforge.jasa.agent.SimpleTradingAgent
import net.sourceforge.jabm.SimulationTime

import scala.slick.driver.MySQLDriver.simple._
import java.text.SimpleDateFormat
import RelationalTables._
import Database.threadLocalSession
import net.sourceforge.jasa.agent.SimpleTradingAgent

class OrderBook {
  
	implicit def orderToJasa(o: Order) = {
		val result = new net.sourceforge.jasa.market.Order()
		result.setPrice(o.price.toDouble)
		result.setQuantity(o.aggregateSize.toInt)
		result.setAgent(new SimpleTradingAgent())
		result.setIsBid(o.buySellInd equals "B")
		result
	}

	val book = new FourHeapOrderBook()
  
	def insert(o: Order, timeStamp: Long) = {
		val order = orderToJasa(o)
		order.setTimeStamp(new SimulationTime(timeStamp))
		if (order.isAsk()) {
			book.insertUnmatchedAsk(order)
		} else {
			book.insertUnmatchedBid(order)
		}
		book.matchOrders()
	}
	
	def printState = {
	  book.printState()
	}
	
}


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
				
			val allOrdersQueryOO = for {
			  event <- events 
			  order <- event.order
			} yield (order, event.timeStamp)

			val allRevisionsQuery = for {
			  event <- events 
			  orderHistory <- event.orderHistory 
			} yield (event.timeStamp, BigDecimal(0), event.eventType)
			
			val allEventsQuery = 
			  (allTransactionsQuery union allOrdersQuery union 
			      allRevisionsQuery).sortBy(_._1)
			
//			println(allEventsQuery.selectStatement)
//			for((t, price, eventType) <- allEventsQuery) {
//			  println(new java.util.Date(t) + ": " + price + " (" + eventType + ")")
//			}
			      
			val orderBook = new OrderBook()
			for((order, timeStamp) <- allOrdersQueryOO) {
				val result = orderBook.insert(order, timeStamp)
				println(result)
			}
			
			orderBook.printState
		}

  }
}
