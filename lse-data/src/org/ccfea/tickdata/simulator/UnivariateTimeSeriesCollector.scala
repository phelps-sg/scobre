package org.ccfea.tickdata.simulator

import net.sourceforge.jabm.SimulationTime

/**
 * (C) Steve Phelps 2013
 */
abstract class UnivariateTimeSeriesCollector(val dataCollector: MarketState => Option[AnyVal])
    extends MarketStateDataCollector[(Option[SimulationTime], Option[AnyVal])]{

  def collectData(state: MarketState) = (state.time, dataCollector(state))

  def outputResult(data: Iterable[(Option[SimulationTime], Option[AnyVal])]) = {
    for ((t, price) <- data) {
      out.println(t.get.getTicks + "\t" + (price match {
        case Some(p) => p.toString()
        case None => "NaN"
      }))
    }
    Unit
  }

}
