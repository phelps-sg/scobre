package org.ccfea.tickdata.replayer

import org.ccfea.tickdata.collector.PriceLevelsCollector
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.simulator.MarketState
import org.ccfea.tickdata.storage.csv.MultivariateCsvDataCollator

class PriceLevelsCSVReplayer(val eventSource: Iterable[TickDataEvent],
                               val outFileName: Option[String],
                               val marketState: MarketState,
                               val maxLevels: Int)
  extends PriceLevelsCollector with MultivariateCsvDataCollator
