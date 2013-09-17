package org.ccfea.tickdata

import net.sourceforge.jasa.market._
import scala.slick.driver.MySQLDriver.simple._
import java.text.SimpleDateFormat
import RelationalTables._

// Use the implicit threadLocalSession
import Database.threadLocalSession

 object OrderReplay {


  def main(args: Array[String]) {
    
		val host = args(0)
		val user = args(1)
		val password = args(2)
		val port = if (args.length < 4) "3306" else args(3)

		val url = 
		  "jdbc:mysql://%s:%s/lse_tickdata?user=%s&password=%s".format(
				  										host, port, user, password)

		Database.forURL(url, driver="com.mysql.jdbc.Driver") withSession {
	  
			val q = for {
			  (ev, tr) <- events.sortBy(_.timeStamp) leftJoin 
			  	transactions on (_.transactionID is _.transactionID)
			} yield (ev.timeStamp, tr.tradePrice.?)
			println(q.selectStatement)
			
			val q1 = for {
				ev <- events sortBy(_.timeStamp)
			    tr <- transactions if ev.transactionID === tr.transactionID
			} yield (ev.timeStamp, tr.tradePrice)
			
			println(q1.selectStatement)
			
			for((t, price) <- q) {
			  println(new java.util.Date(t) + ": " + price)
			}
			
		}

  }
}
