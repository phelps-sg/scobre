package org.ccfea.tickdata

import scala.slick.driver.MySQLDriver.simple._
import org.ccfea.tickdata.conf.ReplayConf
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.simulator.{MarketStateDataCollector, MarketState, OrderBookSnapshotter, OrderReplayer}
import java.text.DateFormat
import java.util.{Calendar, GregorianCalendar, Date}
import grizzled.slf4j.Logger
import net.sourceforge.jabm.SimulationTime

object OrderReplay {

  val logger = Logger("org.ccfea.tickdata.OrderReplay")

  class HBaseMidPriceCollector(val selectedAsset: String, val withGui: Boolean = false, val outFileName: Option[String] = None,
                                val startDate: Option[Date], val endDate: Option[Date])
    extends MarketStateDataCollector with HBaseRetriever {

      def collectData(state: MarketState) = (state.time, state.midPrice)

      def outputResult(data: Any) = {
        data match {
          case timeSeries: Iterable[(Option[SimulationTime], Option[Double])] => {
            for ((t, price) <- timeSeries) {
              out.println(t.get.getTicks + "\t" + (price match {
                case Some(p) => p.toString()
                case None => "NaN"
              }))
            }
            Unit
          }
        }
      }
  }
//
//  class HBaseOrderBookSnapshotter(val selectedAsset: String, val time: Date, outFileName: Option[String])
//      extends OrderBookSnapshotter with HBaseRetriever {
//
//    def calStart = {
//      val calEnd = new GregorianCalendar()
//      calEnd.setTime(time)
//      val calStart = new GregorianCalendar()
//      calStart.set(Calendar.YEAR, calEnd.get(Calendar.YEAR))
//      calStart.set(Calendar.MONTH, calEnd.get(Calendar.MONTH))
//      calStart.set(Calendar.DAY_OF_MONTH, calEnd.get(Calendar.DAY_OF_MONTH))
//      calStart
//    }
//
//    def calEnd = {
//      val calEnd = new GregorianCalendar()
//      calEnd.setTime(time)
//      calEnd.add(Calendar.SECOND, 100)
//      calEnd
//    }
//
//    override def startDate = Some(calStart.getTime)
//    override def endDate = Some(calEnd.getTime)
//  }
//
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

    val replayer = new HBaseMidPriceCollector(conf.tiCode(), conf.withGui(), conf.outFileName.get, startDate, endDate)
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


