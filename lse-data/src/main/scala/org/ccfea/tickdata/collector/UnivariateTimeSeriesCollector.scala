package org.ccfea.tickdata.collector

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.simulator.{AuctionState, MarketState}

/**
 * A market-state data-collector which collates a single variable from the market as a time-series.
 * Each element of the series is of type Option[AnyVal].  When the times-series is written,
 * any element that is None is mapped to NaN (not a number).
 *
 * (C) Steve Phelps 2013
 */
trait UnivariateTimeSeriesCollector
    extends MarketStateDataCollector[(Option[SimulationTime], Option[AnyVal])] {

  def dataCollector: MarketState => Option[AnyVal]

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
