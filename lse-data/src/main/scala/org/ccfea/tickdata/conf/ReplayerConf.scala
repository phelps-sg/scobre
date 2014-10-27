package org.ccfea.tickdata.conf

/**
 * (C) Steve Phelps 2014
 */
class ReplayerConf(args: Seq[String]) extends ReplayConf(args) {
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
  val shuffle = opt[Boolean](default = Some(false), descr="Shuffle the order of the events prior to simulation")
}
