package org.ccfea.tickdata

import scala.slick.driver.MySQLDriver.simple._
import org.ccfea.tickdata.conf.ReplayConf
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.simulator._
import java.text.DateFormat
import java.util.{Calendar, GregorianCalendar, Date}
import grizzled.slf4j.Logger
import net.sourceforge.jabm.SimulationTime
import scala.Some

object OrderReplay {

  val logger = Logger("org.ccfea.tickdata.OrderReplay")

  class HBasePriceCollector(dataCollector: MarketState => (Option[SimulationTime], Option[Double]),
                                val selectedAsset: String, val withGui: Boolean = false,
                                val outFileName: Option[String] = None,
                                val startDate: Option[Date], val endDate: Option[Date])
    extends UnivariateTimeSeriesCollector(dataCollector) with HBaseRetriever

  def parseDate(date: Option[String]): Option[Date] = date match {
    case None => None
    case Some(dateStr) =>  Some(DateFormat.getDateInstance(DateFormat.SHORT).parse(dateStr))
  }

  def main(args: Array[String]) {

    val conf = new ReplayConf(args)

    val startDate = parseDate(conf.startDate.get)
    val endDate = parseDate(conf.endDate.get)

    logger.debug("startDate = " + startDate)
    logger.debug("endDate = " + endDate)

    val replayer =
      new HBasePriceCollector( (state: MarketState) => (state.time, state.midPrice),
                                  conf.tiCode(), conf.withGui(), conf.outFileName.get, startDate, endDate)
    replayer.run

//    val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(conf.startDate.get.get)
//    println(date)
//    val snapShot = new HBaseOrderBookSnapshotter(conf.tiCode(), date, conf.outFileName.get)
//    snapShot.run
  }

}

//
//object StartServer {
//
//  def main(args: Array[String]) {
//
//    val conf = new DbConf(args)
//    val server = new OrderReplayServer(conf.url(), conf.driver())
//  }
//}



//
//class OrderReplayServer(val url: String, val driver: String) extends Actor {
//
//  def receive = {
//      case cmd @ OrderReplay(_, _, _, _, _) =>
//        cmd.run
//    }
//}


