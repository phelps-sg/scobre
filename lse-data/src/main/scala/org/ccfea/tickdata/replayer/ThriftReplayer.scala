package org.ccfea.tickdata.replayer

import org.ccfea.tickdata.collector.MultivariateTimeSeriesCollector
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.simulator.MarketState
import org.ccfea.tickdata.storage.thrift.MultivariateThriftCollator

class ThriftReplayer(val eventSource: Iterable[TickDataEvent],
                     val dataCollectors: Map[String, MarketState => Option[AnyVal]],
                     val marketState: MarketState)
  extends MultivariateTimeSeriesCollector with MultivariateThriftCollator

