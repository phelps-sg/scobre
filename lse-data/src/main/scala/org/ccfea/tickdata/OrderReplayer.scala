package org.ccfea.tickdata

import org.ccfea.tickdata.event.OrderReplayEvent
import org.ccfea.tickdata.storage.csv.CsvCollator

import scala.Some
import scala.util.Random

import org.ccfea.tickdata.conf.ReplayConf
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.simulator._

import java.text.DateFormat
import java.util. Date
import grizzled.slf4j.Logger

/**
 * The main application for running order-book reconstruction simulations.
 *
 * (C) Steve Phelps 2014
 */
object OrderReplayer extends ReplayApplication {

  val logger = Logger("org.ccfea.tickdata.OrderReplayer")

  def main(args: Array[String]) {

    // Parse command-line options
    implicit val conf = new ReplayConf(args)
    // The method which will fetch the datum of interest from the state of the market
    val getPropertyMethod = classOf[MarketState].getMethod(conf.property())

    simulateAndCollate {
      getPropertyMethod invoke _
    }

    // Alternatively we can hard-code the collation function as follows.

    // Simulate and collate the best bid price
//      simulateAndCollate {
//        _.quote.bid
//      }

    // Simulate and collate transaction prices in event-time
//      simulateAndCollate {
//        _.lastTransactionPrice
//      }
  }

  /**
   * Simulate the matching and submission of orders to the LOB and
   * collate properties of the market as a time-series.
   *
   * @param dataCollector   A function for fetching the data of interest
   * @param conf            Command-line options
   */
  def simulateAndCollate(dataCollector: MarketState => Option[AnyVal])
                          (implicit conf: ReplayConf) = {

    var eventSource: Iterable[OrderReplayEvent] =
      new HBaseRetriever(selectedAsset = conf.tiCode(),
                          startDate =  parseDate(conf.startDate.get),
                          endDate = parseDate(conf.endDate.get))

    if (conf.shuffle()) eventSource = Random.shuffle(eventSource.iterator.toList)

    class Replayer(val eventSource: Iterable[OrderReplayEvent],
                    val outFileName: Option[String], val withGui: Boolean,
                    val dataCollector: MarketState => Option[AnyVal])
        extends UnivariateTimeSeriesCollector with CsvCollator

    new Replayer(eventSource,outFileName = conf.outFileName.get,
                                          withGui = conf.withGui(), dataCollector).run()
  }


}


