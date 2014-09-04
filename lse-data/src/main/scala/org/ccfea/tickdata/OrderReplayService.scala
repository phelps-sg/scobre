package org.ccfea.tickdata

import grizzled.slf4j.Logger
import org.apache.thrift.server.{TThreadPoolServer, TSimpleServer}
import org.apache.thrift.transport.TServerSocket
import org.ccfea.tickdata.collector.{MultivariateTimeSeriesCollector, UnivariateTimeSeriesCollector}
import org.ccfea.tickdata.conf.{BuildInfo, ServerConf}

import org.ccfea.tickdata.event.OrderReplayEvent
import org.ccfea.tickdata.simulator.MarketState
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.storage.thrift.MultivariateThriftCollector
import org.ccfea.tickdata.thrift.OrderReplay
import org.ccfea.tickdata.thrift.TimeSeriesDatum

import collection.JavaConversions._

import scala.collection.mutable.ArrayBuilder

/**
 * A server that provides order-replay simulation results over the network.
 * It uses Apache Thrift so that clients can easily be written in other languages.
 *
 * (C) Steve Phelps 2014
 */
object OrderReplayService extends ReplayApplication {

  val logger = Logger("org.ccfea.tickdata.OrderReplayService")

  def main(args: Array[String]): Unit = {

    val conf = new ServerConf(args)
    val port: Int = conf.port()

    val processor = new org.ccfea.tickdata.thrift.OrderReplay.Processor(new OrderReplay.Iface {

      override def replay(assetId: String, variables: java.util.List[String],
                            startDate: String, endDate: String): java.util.List[java.util.Map[String,java.lang.Double]] = {

        logger.info("Fetching data for " + assetId + " between " + startDate + " and " + endDate)

        logger.info("Starting simulation... ")

        val hbaseSource: Iterable[OrderReplayEvent] =
          new HBaseRetriever(selectedAsset = assetId,
            startDate =  parseDate(Some(startDate)),
            endDate = parseDate(Some(endDate)))

        class Replayer(val eventSource: Iterable[OrderReplayEvent] = hbaseSource,
                       val withGui: Boolean = false,
                       val dataCollectors: Map[String, MarketState => Option[AnyVal]])
          extends MultivariateTimeSeriesCollector with MultivariateThriftCollector

        // Take the list of variables, find the method to retrieve the
        //  data for each variable (a function of MarketState),
        //  and then produce a map of variables and methods, i.e. the collectors for the simulation.
        def variableToMethod(variable: String): MarketState => Option[AnyVal] = {
          // Use reflection to convert a variable name into a method
          classOf[MarketState].getMethod(variable) invoke _
        }
        val collectors: Seq[(String, MarketState => Option[AnyVal])] =
          for(variable <- variables) yield (variable, variableToMethod(variable))

        val replayer =
          new Replayer(dataCollectors = Map() ++ collectors)

        replayer.run()

        logger.info("done.")

        replayer.result
      }
    })

    val serverTransport = new TServerSocket(port)
    val server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor))

    logger.info("CCFEA order-replay server version " + BuildInfo.version)
    logger.info("Server running on port " + port + "... ")
    server.serve()
    logger.info("Server terminated.")
  }

}
