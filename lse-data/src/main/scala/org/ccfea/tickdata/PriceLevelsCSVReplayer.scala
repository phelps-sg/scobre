package org.ccfea.tickdata

import org.ccfea.tickdata.collector.{PriceLevelsCollector, UnivariateTimeSeriesCollector}
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.simulator.MarketState
import org.ccfea.tickdata.storage.csv.{MultivariateCsvDataCollator, UnivariateCsvDataCollator}

class PriceLevelsCSVReplayer(val eventSource: Iterable[TickDataEvent],
                               val outFileName: Option[String],
                               val marketState: MarketState,
                               val maxLevels: Int)
  extends PriceLevelsCollector with MultivariateCsvDataCollator
