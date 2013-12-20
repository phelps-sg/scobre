package org.ccfea.tickdata

import org.ccfea.tickdata.conf.ReplayConf
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.simulator._
import java.text.DateFormat
import java.util. Date
import grizzled.slf4j.Logger
import scala.Some

object OrderReplay {

  val logger = Logger("org.ccfea.tickdata.OrderReplay")

  def main(args: Array[String]) {

    implicit val conf = new ReplayConf(args)
    val getPropertyMethod = classOf[MarketState].getMethod(conf.property())

    simulateAndCollate {
      getPropertyMethod invoke _
    }

//    simulateAndCollate {
//      _.quote.bid
//    }
//
//    simulateAndCollate {
//      _.lastTransactionPrice
//    }
  }

  def parseDate(date: Option[String]): Option[Date] = date match {
    case None => None
    case Some(dateStr) =>  Some(DateFormat.getDateInstance(DateFormat.SHORT).parse(dateStr))
  }

  def simulateAndCollate(dataCollector: MarketState => Option[AnyVal])(implicit conf: ReplayConf) = {

    val eventSource =
      new HBaseRetriever(selectedAsset = conf.tiCode(),
                          startDate =  parseDate(conf.startDate.get),
                          endDate = parseDate(conf.endDate.get))

    val replayer =
      new UnivariateTimeSeriesCollector(eventSource, outFileName = conf.outFileName.get,
                                          withGui = conf.withGui(), dataCollector)

    replayer.run()
  }

  implicit def AnyRefToOptionAnyVal(x: AnyRef): Option[AnyVal] = x match {
    case Some(double: Double) => Some(double)
    case Some(long: Long) => Some(long)
    case l: java.lang.Long => Some(l)
    case None => None
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


