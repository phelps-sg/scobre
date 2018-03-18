package org.ccfea.tickdata.conf

/**
 * (C) Steve Phelps 2015
 */
class ReplayerConf(args: Seq[String]) extends ReplayConf(args) {

  banner("""Usage: OrderReplay [OPTION]...
           |Replay tick data through an order-book simulator and collect data
           |on the state of the market.
           |Options:
           |""".stripMargin)

  footer("\nFor more details, consult the readme.")

  val maxLevels = opt[Int](default = Some(100), descr="Maximum number of price levels to output per tick")
  val priceLevels = opt[Boolean](default = Some(false), descr="Collect price levels")
  val withGui = opt[Boolean](default = Some(false), descr="Provide a graphical visualisation of the order-book")
  val maxNumEvents = opt[Int]()
  val tiCode = opt[String](required = true, descr = "The ISIN number of the asset to replay")
  val outFileName = opt[String](required = false, descr = "The name of the file to output data to")
  val startDate = opt[String](required = false, descr = "The earliest event to replay")
  val endDate = opt[String](required = false, descr = "The date of the last event to replay")
  val property = opt[String](default = Some("midPrice"), descr = "Comma-separated list of variables to retrieve")
  val shuffle = opt[Boolean](default = Some(false), descr="Shuffle the order of the events prior to simulation")
  val offsetting = opt[String](default = Some("none"), descr="One of none, mid, same, opposite")
  val proportionShuffling = opt[Double](default = Some(1.0), descr="Proportion of the events to shuffle")
  val shuffleWindowSize = opt[Int](default = Some(1000), descr="Window size for shuffled replay")

  verify()
}
