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
		val url = 
		  "jdbc:mysql://%s/lse_tickdata?user=%s&password=%s".format(
				  										host, user, password)

		Database.forURL(url, driver="com.mysql.jdbc.Driver") withSession {
	  
			 val joinQuery = 
			   (events leftJoin transactions on (_.transactionID === _.transactionID)) //.sortBy(_.timeStamp) .sortBy(_.messageSequenceNumber)
			   
			println(joinQuery.selectStatement)
			
		  	for((event, transaction) <- joinQuery)  {
				println(event)
				println(transaction)
			}
		}

  }
}