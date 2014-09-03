package org.ccfea.tickdata

import org.apache.thrift.server.{TThreadPoolServer, TSimpleServer}
import org.apache.thrift.transport.TServerSocket

import org.ccfea.tickdata.event.OrderReplayEvent
import org.ccfea.tickdata.simulator.{UnivariateTimeSeriesCollector, MarketState}
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.storage.thrift.ThriftCollator
import org.ccfea.tickdata.thrift.OrderReplay
import org.ccfea.tickdata.thrift.TimeSeriesDatum

import collection.JavaConversions._

import scala.collection.mutable.ArrayBuilder

/**
 * (C) Steve Phelps 2014
 */
object OrderReplayService extends ReplayApplication {

  def main(args: Array[String]): Unit = {

    val processor = new org.ccfea.tickdata.thrift.OrderReplay.Processor(new OrderReplay.Iface {

      override def replay(assetId: String, property: String,
                            startDate: String, endDate: String): java.util.List[TimeSeriesDatum] = {
        val hbaseSource: Iterable[OrderReplayEvent] =
          new HBaseRetriever(selectedAsset = assetId,
            startDate =  parseDate(Some(startDate)),
            endDate = parseDate(Some(endDate)))

        class Replayer(val eventSource: Iterable[OrderReplayEvent],
                       val withGui: Boolean = false,
                       val dataCollector: MarketState => Option[AnyVal])
          extends UnivariateTimeSeriesCollector with ThriftCollator

        val replayer =
          new Replayer(hbaseSource, dataCollector = classOf[MarketState].getMethod(property) invoke _)

        replayer.run()

        replayer.result
      }
    })
    val serverTransport = new TServerSocket(9090)
    val server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor))
    server.serve()
  }

}
