package org.ccfea.tickdata

import java.util.Date

import grizzled.slf4j.Logger
import org.ccfea.tickdata.collector.MultivariateTimeSeriesCollector
import org.ccfea.tickdata.conf.ServerConf
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.order.LimitOrder
import org.ccfea.tickdata.order.offset.{MidPriceOffsetOrder, Offsetting, OppositeSideOffsetOrder, SameSideOffsetOrder}
import org.ccfea.tickdata.replayer.{MultivariateCSVReplayer, ThriftReplayer}
import org.ccfea.tickdata.simulator.Quote
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.storage.shuffled._
import org.ccfea.tickdata.storage.shuffled.copier.{OrderSignCopier, PriceCopier, TickCopier, VolumeCopier}
import org.ccfea.tickdata.thrift.{DataFrame, OrderReplay}

import scala.collection.JavaConverters._

/**
 * A server that provides order-replay simulation results over the network.
 * It uses Apache Thrift so that clients can easily be written in other languages.
 *
 * (C) Steve Phelps 2014
 */
class OrderReplayService(val conf: ServerConf) extends ScobreApplication with OrderReplay.Iface {

  val port: Int = conf.port()

  var tickCache = Map[(String, Offsetting.Value, Option[(Long, Long)]), Seq[TickDataEvent]]()

  val logger = Logger("org.ccfea.tickdata.OrderReplayService")

  def getOffsettedTicks(ticks: Seq[TickDataEvent], offsetting: Offsetting.Value) = {
    val marketState = newMarketState(conf)
    offsetting match {
      case Offsetting.NoOffsetting =>
        ticks
      case Offsetting.MidPrice =>
        new OffsettedTicks(marketState, ticks,
                            (limitOrder: LimitOrder, quote: Quote) => new MidPriceOffsetOrder(limitOrder, quote))
      case Offsetting.SameSide =>
        new OffsettedTicks(marketState, ticks,
                            (limitOrder: LimitOrder, quote: Quote) => new SameSideOffsetOrder(limitOrder, quote))
      case Offsetting.OppositeSide =>
        new OffsettedTicks(marketState, ticks,
                              (limitOrder: LimitOrder, quote: Quote) => new OppositeSideOffsetOrder(limitOrder, quote))
    }
  }

  def getShuffledData(assetId: String,
                          proportionShuffling: Double,
                          windowSize: Int, intraWindow: Boolean,
                          offsetting: Offsetting.Value, shuffledAttribute: ShuffledAttribute.Value,
                          dateRange: Option[(Long, Long)]): Seq[TickDataEvent] = {
    val ticks = if (tickCache.contains((assetId, offsetting))) {
      tickCache((assetId, offsetting, dateRange))
    } else {
      val start = dateRange match {
        case None => None
        case Some((t0, t1)) => Some(new Date(t0))
      }
      val end = dateRange match {
        case None => None
        case Some((t0, t1)) => Some(new Date(t1))
      }
      val originalData = new HBaseRetriever(selectedAsset = assetId, startDate = start, endDate = end).toList
      val offsettedTicks = getOffsettedTicks(originalData, offsetting).toList
      tickCache += ((assetId, offsetting, dateRange) -> offsettedTicks)
      offsettedTicks
    }

    val swapper = shuffledAttribute match {
      case ShuffledAttribute.AllAttributes => new TickCopier()
      case ShuffledAttribute.Volume => new VolumeCopier()
      case ShuffledAttribute.OrderSign => new OrderSignCopier()
      case ShuffledAttribute.Price => new PriceCopier()
    }

    if (intraWindow)
      new IntraWindowRandomPermutation(ticks, proportionShuffling, windowSize, swapper)
    else
      new RandomPermutation(ticks, proportionShuffling, windowSize, swapper)

  }

  def executeShuffledReplay(assetId: String, variables: List[String],
                                      proportionShuffling: Double, windowSize: Int, intraWindow: Boolean,
                                      offsetting: Int, attribute: Int,
                                      dateRange: Option[(Long, Long)]): DataFrame = {
    logger.info("Shuffled replay for " + assetId + " with windowSize " + windowSize +
                      ", offsetting " + offsetting + " and percentage " + proportionShuffling)
    dateRange match {
      case Some((t0, t1)) =>
        logger.info("between " + new Date(t0) + " and " + new Date(t1))
      case None =>
    }
    val marketState = newMarketState(conf)
    val shuffledTicks =
      getShuffledData(assetId, proportionShuffling, windowSize, intraWindow,
                      Offsetting(offsetting), ShuffledAttribute(attribute), dateRange)
    val replayer =
      new ThriftReplayer(shuffledTicks, dataCollectors(variables), marketState)
    runSimulation(replayer)
    new DataFrame(replayer.timestamps, replayer.result)
  }

  def getTicks(assetId: String, variables: java.util.List[String],
               startDateTime: Long,
               endDateTime: Long): HBaseRetriever = {
    val startDate = new Date(startDateTime)
    val endDate = new Date(endDateTime)
    logger.info("Using data for " + assetId + " between " + startDate + " and " + endDate)
    new HBaseRetriever(selectedAsset = assetId, startDate = Some(startDate), endDate = Some(endDate))
  }

  def runSimulation(replayer: MultivariateTimeSeriesCollector) = {
    logger.info("Starting simulation... ")
    replayer.run()
    logger.info("done.")
  }

  override def replay(assetId: String, variables: java.util.List[String],
                        startDateTime: Long,
                        endDateTime: Long): DataFrame = {
    val marketState = newMarketState(conf)
    val ticks = getTicks(assetId, variables, startDateTime, endDateTime)
    val replayer = new ThriftReplayer(ticks, dataCollectors(List() ++ variables.asScala), marketState)
    runSimulation(replayer)
    ticks.closeConnection()
    return new DataFrame(replayer.timestamps, replayer.result)
  }

  override def replayToCsv(assetId: String, variables: java.util.List[String],
                            startDateTime: Long, endDateTime: Long,
                            csvFileName: String): Long = {
    val marketState = newMarketState(conf)
    val ticks = getTicks(assetId, variables, startDateTime, endDateTime)
    val replayer =
      new MultivariateCSVReplayer(ticks, dataCollectors(List() ++ variables.asScala), marketState, Some(csvFileName))
    runSimulation(replayer)
    ticks.closeConnection()
    return 0
  }

  override def shuffledReplayDateRange(assetId: String, variables: java.util.List[String],
                                proportionShuffling: Double, windowSize: Int, intraWindow: Boolean,
                                  offsetting: Int, attribute: Int, startDateTime: Long, endDateTime: Long): DataFrame = {
    executeShuffledReplay(assetId, List() ++ variables.asScala, proportionShuffling, windowSize,
                            intraWindow, offsetting, attribute, Some((startDateTime, endDateTime)))
  }

  override def shuffledReplay(assetId: String, variables: java.util.List[String], proportionShuffling: Double,
                                windowSize: Int, intraWindow: Boolean,
                                offsetting: Int,
                              attribute: Int): DataFrame = {
    executeShuffledReplay(assetId, List() ++ variables.asScala, proportionShuffling, windowSize, intraWindow,
                                offsetting, attribute, None)
  }

}
