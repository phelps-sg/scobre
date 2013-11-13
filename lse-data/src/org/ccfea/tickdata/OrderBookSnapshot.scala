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

object OrderBookSnapshot {

  val logger = Logger("org.ccfea.tickdata.OrderReplay")

  class HBaseOrderBookSnapshotter(val selectedAsset: String, val withGui: Boolean = true,
                                   val t: SimulationTime, val outFileName: Option[String])
        extends OrderBookSnapshotter with HBaseRetriever {

    def startDate = {
      // Start date is the beginning of the day on which the snapshot is required
      val startCal = new GregorianCalendar()
      startCal.setTime(new Date(t.getTicks))
      startCal.set(Calendar.HOUR, 0)
      startCal.set(Calendar.MINUTE, 0)
      startCal.set(Calendar.SECOND, 0)
      Some(startCal.getTime)
    }

    def endDate = {
      // End date is 5 minutes after the time at which the snapshot is required
      val endCal = new GregorianCalendar()
      endCal.setTime(new Date(t.getTicks))
      endCal.add(Calendar.MINUTE, 5)
      Some(endCal.getTime)
    }
  }

  def parseDate(date: Option[String]): Option[Date] = date match {
    case None => None
    case Some(dateStr) =>  Some(DateFormat.getDateInstance(DateFormat.SHORT).parse(dateStr))
  }

  def main(args: Array[String]) {

    val conf = new ReplayConf(args)

//    val startDate = parseDate(conf.startDate.get)
//    val endDate = parseDate(conf.endDate.get)

//    logger.debug("startDate = " + startDate)
//    logger.debug("endDate = " + endDate)

//    val replayer =
//      new HBasePriceCollector( (state: MarketState) => (state.time, state.midPrice),
//                                  conf.tiCode(), conf.withGui(), conf.outFileName.get, startDate, endDate)
//    replayer.run()

    val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(conf.startDate.get.get)
    println(date)
    val snapShotter =
      new HBaseOrderBookSnapshotter(conf.tiCode(), conf.withGui(),
                                      new SimulationTime(date.getTime), conf.outFileName.get)
    snapShotter.run
  }
}

