package org.ccfea.tickdata

import scala.slick.driver.MySQLDriver.simple._
import org.ccfea.tickdata.hbase.HBaseOrderReplay
import org.ccfea.tickdata.conf.ReplayConf

object OrderReplay {

  def main(args: Array[String]) {

    val conf = new ReplayConf(args)

//    val replay = new SqlOrderReplay(conf.url(), conf.driver(), conf.tiCode(), conf.withGui(), None) // TODOconf.maxNumEvents.)
    val replay = new HBaseOrderReplay(conf.tiCode(), conf.withGui(), None) // TODOconf.maxNumEvents.)
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


