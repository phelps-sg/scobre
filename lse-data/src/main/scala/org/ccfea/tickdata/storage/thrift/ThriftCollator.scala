package org.ccfea.tickdata.storage.thrift

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.simulator.MarketStateDataCollector
import org.ccfea.tickdata.thrift.TimeSeriesDatum

/**
 * (C) Steve Phelps 2014
 */
trait ThriftCollator  extends MarketStateDataCollector[(Option[SimulationTime], Option[AnyVal])] {

  var result: List[TimeSeriesDatum] = List()

  def outputResult(data: Iterable[(Option[SimulationTime], Option[AnyVal])]) = {
    for ((t, price) <- data) {
      result ::= new TimeSeriesDatum( t.get.getTicks, price match {
        case Some(p:Double) => p
        case Some(l:Long) => l.toDouble
        case None => Double.NaN
        case _ => Double.NaN
      })
    }
  }

}
