package org.ccfea.tickdata

import org.ccfea.tickdata.conf.{ReplayConf, ReplayerConf, SnapshotConf}
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.simulator._
import java.text.DateFormat
import java.util.{Calendar, Date, GregorianCalendar}

import grizzled.slf4j.Logger
import net.sourceforge.jabm.SimulationTime

import scala.Some

object OrderBookSnapshot extends ReplayApplication {

  val logger = Logger("org.ccfea.tickdata.OrderReplay")

  def main(args: Array[String]) {

    val conf = new SnapshotConf(args)

    val eventSource =
      new HBaseRetriever(selectedAsset = conf.tiCode(),
                            startDate =  parseDate(conf.startDate.get),
                            endDate = parseDate(conf.endDate.get))

    val marketState = newMarketState(conf)
    if (conf.withGui()) new OrderBookView(marketState)

    val snapshotter =
      new OrderBookSnapshotter(eventSource, conf.outFileName.get, marketState,
                                  maxLevels = conf.maxLevels())

    snapshotter.run
  }
}

