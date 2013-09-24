package org.ccfea.tickdata

import scala.slick.driver.MySQLDriver.simple._
import RelationalTables._
import Database.threadLocalSession

import java.text.SimpleDateFormat

import net.sourceforge.jasa.market._
import net.sourceforge.jasa.agent.SimpleTradingAgent
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer

import net.sourceforge.jabm.SimulationTime

import javax.swing.JFrame
import javax.swing.SwingUtilities

import collection.JavaConversions._

//import scala.tools.nsc._

class MarketState {

	val book = new FourHeapOrderBook()
	var lastChanged: Option[SimulationTime] = None
	
	implicit def orderToJasa(o: Order) = {
		val result = new net.sourceforge.jasa.market.Order()
		result.setPrice(o.price.toDouble)
		result.setQuantity(o.aggregateSize.toInt)
		result.setAgent(new SimpleTradingAgent())
		result.setIsBid(o.buySellInd equals "B")
		result.setTimeStamp(lastChanged.get)
		//TODO hold reference to JASA order from slick case class
		result
	}

	def processOrder(o: Order, timeStamp: Long) = {
		if (o.marketMechanismType equals "LO") {
			processLimitOrder(o, timeStamp)
		} else if (o.marketMechanismType equals "MO") {
			processMarketOrder(o, timeStamp)
		} else {
			throw new RuntimeException("Unknown market mechanism type " + o)
		}
	}
		
	def processLimitOrder(o: Order, timeStamp: Long) = {
		lastChanged = Some(new SimulationTime(timeStamp))
		val order = orderToJasa(o)
		if (order.isAsk()) {
			book.insertUnmatchedAsk(order)
		} else {
			book.insertUnmatchedBid(order)
		}
		book.matchOrders()
	}
	
	def processMarketOrder(o: Order, timeStamp: Long) = {
		val otherSide = 
		if (o.buySellInd equals "B") book.getUnmatchedAsks() 
			else book.getUnmatchedBids()
		var qty = o.aggregateSize
		for(potentialMatch <- otherSide if qty >= potentialMatch.getQuantity()) {
			val limitOrderQty = potentialMatch.getQuantity()
			book.remove(potentialMatch)
			qty = qty - limitOrderQty
		}
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

class MarketStateWithGUI extends MarketState {

	val view: OrderBookView = new OrderBookView(this)

	override def processOrder(o: Order, timeStamp: Long) = {
		val result = super.processOrder(o, timeStamp)
		println(o)
		view.update
		result
	}
}

class OrderBookView(val market: MarketState) {
	val auctioneer = new ContinuousDoubleAuctioneer()
	auctioneer.setOrderBook(market.book)
	val orderBookView = new net.sourceforge.jasa.view.OrderBookView()
	orderBookView.setAuctioneer(auctioneer)
	orderBookView.setMaxDepth(10)
	orderBookView.afterPropertiesSet()
	val myFrame = new JFrame()
	myFrame.add(orderBookView)
	myFrame.pack()
	myFrame.setVisible(true) 

	def update = {
		SwingUtilities.invokeAndWait(new Runnable() {
			def run() = {
			orderBookView.update(); orderBookView.notifyTableChanged()
			}
		}) 
	}
}

class OrderFlow(val orders: Seq[(Order, Long, Long)], val market: MarketState = new MarketState()) {

	def map[B](f: MarketState => B): Seq[B] = {
		orders.map(ev => {
					market.processOrder(ev._1, ev._2); 
					f(market) 
		})
	}
	
}

object OrderReplay {
//
//  def series[B](f: MarketState => B): Seq[B] = {
//		  
//  }

	def main(args: Array[String]) {

		val url = TickDatabase.url(args)
		Database.forURL(url, driver="com.mysql.jdbc.Driver") withSession {
	
			val allTransactionsQuery = for {
				event <- events
				transaction <- event.transaction
			} yield (event.timeStamp, transaction.tradePrice, event.eventType)
				
			val allOrders = for {
				event <- events 
				order <- event.order
			} yield (order, event.timeStamp, event.messageSequenceNumber)

			val allOrdersByTime= allOrders.sortBy(_._2).sortBy(_._3)

			val timeSeries = 
				for {
					market <- new OrderFlow(allOrdersByTime.list)
				} yield (market.lastChanged, market.midPrice)
				
			for( (t, price) <- timeSeries) {
			println(t.get.getTicks() + "\t" + (price match {
				case Some(p) => p.toString()
				case None => "NaN"
			}))
			}
			
		}

	}
}
