package org.ccfea.tickdata.storage.memory

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.collector.MarketStateDataCollector

/**
 * (C) Steve Phelps 2014
 */
trait MemoryCollator  extends MarketStateDataCollector[(Option[SimulationTime], Option[AnyVal])] {

  var result: List[(Long, Double)] = List()

  def outputResult(data: Iterable[(Option[SimulationTime], Option[AnyVal])]) = {
      for ((t, price) <- data) {
        result ::= ( t.get.getTicks, price match {
          case Some(p:Double) => p
          case Some(l:Long) => l.toDouble
          case None => Double.NaN
          case _ => Double.NaN
        })
    }
  }

}
