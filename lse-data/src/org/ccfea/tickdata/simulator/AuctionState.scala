package org.ccfea.tickdata.simulator

/**
 * (C) Steve Phelps 2014
 */
object AuctionState extends Enumeration {
  val startOfDay = Value("start_of_day")
  val batchOpen = Value("batch_open")
  val uncrossing = Value("uncrossing")
  val continuous = Value("continuous")
  val batchClose = Value("batch_close")
  val endOfDay = Value("end_of_day")
}
