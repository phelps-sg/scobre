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

	val book = new FourHeapOrderBook()
	var lastChanged: Option[SimulationTime] = None
   
	implicit def orderToJasa(o: Order) = {
		val result = new net.sourceforge.jasa.market.Order()
		result.setPrice(o.price.toDouble)
		result.setQuantity(o.aggregateSize.toInt)
		result.setAgent(new SimpleTradingAgent())
		result.setIsBid(o.buySellInd equals "B")
		result.setTimeStamp(lastChanged.get)
		result
	}
 
	def insert(o: Order, timeStamp: Long) = {
		lastChanged = Some(new SimulationTime(timeStamp))
		val order = orderToJasa(o)
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
	
	def midPrice: Option[Double] = {
		  val bid = book.getHighestUnmatchedBid() 
		  val ask = book.getLowestUnmatchedAsk()
		  if (bid == null || ask == null) {
			  None
		  } else {
			  Some((bid.getPrice() + ask.getPrice()) / 2.0)
		  }
	}
	
}

class OrderFlow(val orders: Seq[(Order, Long, Long)], val book: OrderBook) {
	
	def map[B](f: OrderBook => B): Seq[B] = {
	  orders.map(ev => {
	    book.insert(ev._1, ev._2); 
	    f(book) 
	  })
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
				
			val allLimitOrders = for {
			  event <- events 
			  order <- event.order
			  if (order.marketMechanismType is "LO")
			} yield (order, event.timeStamp, event.messageSequenceNumber)

			val allRevisionsQuery = for {
			  event <- events 
			  orderHistory <- event.orderHistory 
			} yield (event.timeStamp, BigDecimal(0), event.eventType)
			
			val allEventsQuery = 
			  (allTransactionsQuery union allOrdersQuery union 
			      allRevisionsQuery).sortBy(_._1)
			
			val orderBook = new OrderBook()
			val timeSeries = 
				for {
					orderBookState <- 
						new OrderFlow(
						    allLimitOrders.sortBy(_._2).sortBy(_._3).list, 
						    orderBook)
				} yield (orderBookState.lastChanged, orderBookState.midPrice)
				
			for( (t, price) <- timeSeries) {
			  println(t.get.getTicks() + "\t" + (price match {
			    case Some(p) => p.toString()
			    case None => "NA"
			  }))
			}
			
		}

  }
}
