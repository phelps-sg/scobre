package org.ccfea.tickdata

import org.ccfea.tickdata.collector.UnivariateTimeSeriesCollector
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.order.LimitOrder
import org.ccfea.tickdata.order.offset.{OppositeSideOffsetOrder, MidPriceOffsetOrder, SameSideOffsetOrder}
import org.ccfea.tickdata.storage.csv.UnivariateCsvDataCollator
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.storage.shuffled.{RandomPermutation, OffsettedTicks}

import org.ccfea.tickdata.conf.ReplayerConf
import org.ccfea.tickdata.simulator._

import grizzled.slf4j.Logger

/**
 * The main application for running order-book reconstruction simulations.
 *
 * (C) Steve Phelps 2014
 */
object ReplayOrders extends ReplayApplication {

  val logger = Logger("org.ccfea.tickdata.OrderReplayer")

  def main(args: Array[String]) {

    // Parse command-line options
    implicit val conf = new ReplayerConf(args)
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
                          (implicit conf: ReplayerConf) = {

    val hbaseTicks: Iterable[TickDataEvent] =
      new HBaseRetriever(selectedAsset = conf.tiCode(),
                          startDate =  parseDate(conf.startDate.get),
                          endDate = parseDate(conf.endDate.get))

    class Replayer(val eventSource: Iterable[TickDataEvent],
                    val outFileName: Option[String],
                    val dataCollector: MarketState => Option[AnyVal],
                    val marketState: MarketState)
        extends UnivariateTimeSeriesCollector with UnivariateCsvDataCollator

    val marketState = newMarketState(conf)
    if (conf.withGui()) new OrderBookView(marketState)
    val ticks = if (conf.shuffle()) shuffledTicks(hbaseTicks) else hbaseTicks
    val replayer = new Replayer(ticks, outFileName = conf.outFileName.get, dataCollector, marketState)
    replayer.run()
  }

  def shuffledTicks(ticks: Iterable[TickDataEvent])(implicit conf: ReplayerConf) = {
    val marketState = newMarketState(conf) // This market-state is used to calculate price-offsets before shuffling
    val offsettedTicks = new OffsettedTicks(marketState, ticks,
                                             (lo: LimitOrder, quote: Quote) => new SameSideOffsetOrder(lo, quote))
    new RandomPermutation(offsettedTicks.iterator.toList, conf.proportionShuffling(), conf.shuffleWindowSize())
  }

}
