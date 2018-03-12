package org.ccfea.tickdata

import org.ccfea.tickdata.collector.UnivariateTimeSeriesCollector
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.order.LimitOrder
import org.ccfea.tickdata.order.offset.{MidPriceOffsetOrder, OppositeSideOffsetOrder, SameSideOffsetOrder}
import org.ccfea.tickdata.storage.csv.UnivariateCsvDataCollator
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.storage.shuffled.{OffsettedTicks, RandomPermutation}
import org.ccfea.tickdata.conf.ReplayerConf
import org.ccfea.tickdata.simulator._
import grizzled.slf4j.Logger
import org.ccfea.tickdata.ui.OrderBookView

import scala.swing.{Frame, SimpleSwingApplication}

/**
 * The main application for running order-book reconstruction simulations.
 *
 * (C) Steve Phelps 2016
 */
object ReplayOrders {

  val logger = Logger("org.ccfea.tickdata.OrderReplayer")

  def main(args: Array[String]) {
    val conf = new ReplayerConf(args)
    val app = new ReplayApplication(conf)
    app.run()
  }

}
