package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf
import java.util.Date

/**
 * Parser for command-line options related to order replay.
 *
 * (c) Steve Phelps 2013
 */

class ReplayConf(args: Seq[String]) extends ScallopConf(args) {

  version("org.ccfea.tickdata.OrderReplay 0.7 (c) 2013 Steve Phelps")
  banner("""Usage: OrderReplay [OPTION]...
           |Replay tick data through an order-book simulator and collect data
           |on the state of the market.
           |Options:
           |""".stripMargin)
  footer("\nFor more details, consult the readme.")
  val withGui = opt[Boolean](default = Some(false), descr="Provide a graphical visualisation of the order-book")
  val maxNumEvents = opt[Int]()
  val tiCode = opt[String](required = true, descr = "The ISIN number of the asset to replay")
  val outFileName = opt[String](required = false, descr = "The name of the file to output data to")
  val startDate = opt[String](required = false, descr = "The earliest event to replay")
  val endDate = opt[String](required = false, descr = "The date of the last event to replay")
  val property = opt[String](default = Some("midPrice"), descr = "The data to retrieve")
}

