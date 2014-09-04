package org.ccfea.tickdata.storage.thrift

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.collector.MarketStateDataCollector
import org.ccfea.tickdata.thrift.TimeSeriesDatum
import collection.JavaConversions._

/**
 * (C) Steve Phelps 2014
 */
trait MultivariateThriftCollector
    extends MarketStateDataCollector[(Option[SimulationTime], Map[String,Option[AnyVal]])] {

  val result: java.util.List[java.util.Map[java.lang.String, java.lang.Double]] = new java.util.LinkedList()

  def outputResult(data: Iterable[(Option[SimulationTime], Map[String,Option[AnyVal]])]) = {
    for ((t, bindings) <- data) {
      val javaBindings = new java.util.HashMap[java.lang.String,java.lang.Double]()
      for((variable, value) <- bindings) {
        javaBindings.put(variable, value match {
          case Some(p:Double) => p
          case Some(l:Long) => l.toDouble
          case _ => Double.NaN
        })
      }
      javaBindings.put("t", t.get.getTicks.toDouble / 1000)
      result.add(javaBindings)
    }
  }

}
