package org.ccfea.tickdata.collector

import net.sourceforge.jabm.SimulationTime
import net.sourceforge.jasa.market.Price
import org.ccfea.tickdata.simulator.MarketState

import scala.collection.SortedMap
import scala.collection.immutable.SortedSet

trait PriceLevelsCollector extends
  MarketStateDataCollector[(Option[SimulationTime], SortedMap[String, Option[AnyVal]])] {

  def maxLevels: Int

  def collectData(state: MarketState): (Option[SimulationTime], SortedMap[String, Option[AnyVal]]) = {
    val levels = state.book.signedPriceLevels()
    val tuples =
      for(((price, volume), i) <- levels.zipWithIndex)
        yield ("L%07d".format(i), if (i < levels.size) Some(volume) else None)
    (state.time, SortedMap[String, Option[AnyVal]]() ++ tuples)
  }
}
