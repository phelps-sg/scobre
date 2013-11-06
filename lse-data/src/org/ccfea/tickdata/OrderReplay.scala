package org.ccfea.tickdata

import scala.slick.driver.MySQLDriver.simple._
import org.ccfea.tickdata.hbase.HBaseRetriever
import org.ccfea.tickdata.conf.ReplayConf

class HBaseOrderReplay(val selectedAsset: String, withGui: Boolean = false, outFileName: Option[String] = None)
  extends AbstractOrderReplay(withGui, outFileName) with HBaseRetriever

object OrderReplay {

  def main(args: Array[String]) {
    val conf = new ReplayConf(args)
    val replay = new HBaseOrderReplay(conf.tiCode(), conf.withGui(), conf.outFileName.get)
    replay.run
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


