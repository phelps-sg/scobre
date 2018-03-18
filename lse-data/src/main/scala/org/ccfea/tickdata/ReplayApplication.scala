package org.ccfea.tickdata

import org.ccfea.tickdata.conf.ReplayerConf
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.order.LimitOrder
import org.ccfea.tickdata.order.offset.{MidPriceOffsetOrder, OppositeSideOffsetOrder, SameSideOffsetOrder}
import org.ccfea.tickdata.replayer.{MultivariateCSVReplayer, PriceLevelsCSVReplayer, UnivariateCSVReplayer}
import org.ccfea.tickdata.simulator.{MarketState, Quote}
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.storage.shuffled.{OffsettedTicks, RandomPermutation}
import org.ccfea.tickdata.ui.OrderBookView

import scala.collection.SortedMap

/**
 * Common functionality for all applications which replay tick events and collate data
 * on the evolving market state.
 *
 * (C) Steve Phelps 2015
 */
class ReplayApplication(val conf:ReplayerConf) extends ScobreApplication {

  lazy val hbaseTicks: Iterable[TickDataEvent] =
    new HBaseRetriever(selectedAsset = conf.tiCode(),
      startDate =  parseDate(conf.startDate.toOption),
      endDate = parseDate(conf.endDate.toOption))

  val marketState = newMarketState()
  val view = if (conf.withGui()) new Some(new OrderBookView(marketState)) else None

  lazy val ticks = if (conf.shuffle()) shuffledTicks(hbaseTicks) else hbaseTicks

  def newMarketState(): MarketState = newMarketState(conf)

  def shuffledTicks(ticks: Iterable[TickDataEvent]) = {
    val offsettedTicks = if (conf.offsetting() == "none")  ticks else {
      val marketState = newMarketState() // This market-state is used to calculate price-offsets before shuffling
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

  def properties = List[String]() ++ conf.property().split(',')

  def run() = {
    val replayer =
      if (conf.priceLevels())
        new PriceLevelsCSVReplayer(ticks, conf.outFileName.toOption, marketState, conf.maxLevels())
      else
        new MultivariateCSVReplayer(ticks, dataCollectors(properties), marketState, conf.outFileName.toOption)
    replayer.run()
  }
}
