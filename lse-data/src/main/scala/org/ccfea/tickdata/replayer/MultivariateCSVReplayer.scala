package org.ccfea.tickdata.replayer

import org.ccfea.tickdata.collector.MultivariateTimeSeriesCollector
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.simulator.MarketState
import org.ccfea.tickdata.storage.csv.MultivariateCsvDataCollator

class MultivariateCSVReplayer(val eventSource: Iterable[TickDataEvent],
                  val dataCollectors: Map[String, MarketState => Option[AnyVal]],
                  val marketState: MarketState, val outFileName: Option[String])
  extends MultivariateTimeSeriesCollector with MultivariateCsvDataCollator

