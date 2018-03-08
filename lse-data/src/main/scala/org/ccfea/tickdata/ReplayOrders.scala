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
object ReplayOrders extends ReplayApplication {

  val logger = Logger("org.ccfea.tickdata.OrderReplayer")

  def main(args: Array[String]) {
    implicit val conf = new ReplayerConf(args)
    replayAndCollate(classOf[MarketState].getMethod(conf.property()) invoke _)
  }

  /**
   * Simulate the matching and submission of orders to the LOB and
   * collate properties of the market as a time-series.
   *
   *  e.g. replayAndCollate(_.lastTransactionPrice)
   *
   * @param dataCollector   A function for fetching the data of interest
   * @param conf            Command-line options
   */
  def replayAndCollate(dataCollector: MarketState => Option[AnyVal])
                          (implicit conf: ReplayerConf) = {

    val hbaseTicks: Iterable[TickDataEvent] =
      new HBaseRetriever(selectedAsset = conf.tiCode(),
                          startDate =  parseDate(conf.startDate.toOption),
                          endDate = parseDate(conf.endDate.toOption))

    class Replayer(val eventSource: Iterable[TickDataEvent],
                    val outFileName: Option[String],
                    val dataCollector: MarketState => Option[AnyVal],
                    val marketState: MarketState)
        extends UnivariateTimeSeriesCollector with UnivariateCsvDataCollator

    val marketState = newMarketState(conf)
    if (conf.withGui()) {
      val view = new OrderBookView(marketState)
    }
    val ticks = if (conf.shuffle()) shuffledTicks(hbaseTicks) else hbaseTicks
    val replayer = new Replayer(ticks, outFileName = conf.outFileName.get, dataCollector, marketState)
    replayer.run()
  }

  def shuffledTicks(ticks: Iterable[TickDataEvent])(implicit conf: ReplayerConf) = {
    val offsettedTicks = if (conf.offsetting.get == "none")  ticks else {
        val marketState = newMarketState(conf) // This market-state is used to calculate price-offsets before shuffling
        val offsetFn = conf.offsetting() match {
          case "same" => (lo: LimitOrder, quote: Quote) => new SameSideOffsetOrder(lo, quote)
          case "mid" => (lo: LimitOrder, quote: Quote) => new MidPriceOffsetOrder(lo, quote)
          case "opp" => (lo: LimitOrder, quote: Quote) => new OppositeSideOffsetOrder(lo, quote)
          case _ => throw new RuntimeException("Illegal option: " + conf.offsetting())
        }
        new OffsettedTicks(marketState, ticks, offsetFn)
      }
    new RandomPermutation(offsettedTicks.iterator.toList, conf.proportionShuffling(), conf.shuffleWindowSize())
  }

}
