package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.event.{OrderReplayEvent, Event}
import org.ccfea.tickdata.storage.csv.CsvCollator

/**
 * A market-state data-collector which collates a single variable from the market as a time-series.
 * Each element of the series is of type Option[AnyVal].  When the times-series is written,
 * any element that is None is mapped to NaN (not a number).
 *
 * (C) Steve Phelps 2013
 */
class UnivariateTimeSeriesCollector(val eventSource: Iterable[OrderReplayEvent],
                                      val outFileName: Option[String] = None,
                                      val withGui: Boolean = false,
                                      val dataCollector: MarketState => Option[AnyVal])
    extends MarketStateDataCollector[(Option[SimulationTime], Option[AnyVal])] with CsvCollator {

  /**
   * Collect data from the state of the market during the continuous trading periods.
   *
   * @param state  The current state of the market
   *
   * @return        A datum consisting of a tuple whose first element is the current time, and whose second element
   *                is the value of the single variable we are collecting through the dataCollector or None for
   *                periods where the market is not in the continuous trading state.
   */
  def collectData(state: MarketState) =
    (state.time, if (state.auctionState == AuctionState.continuous) dataCollector(state) else None)

}
