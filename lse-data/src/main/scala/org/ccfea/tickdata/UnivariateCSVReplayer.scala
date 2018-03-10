package org.ccfea.tickdata

import org.ccfea.tickdata.collector.UnivariateTimeSeriesCollector
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.simulator.MarketState
import org.ccfea.tickdata.storage.csv.UnivariateCsvDataCollator

class UnivariateCSVReplayer(val eventSource: Iterable[TickDataEvent],
               val outFileName: Option[String],
               val dataCollector: MarketState => Option[AnyVal],
               val marketState: MarketState)
  extends UnivariateTimeSeriesCollector with UnivariateCsvDataCollator
