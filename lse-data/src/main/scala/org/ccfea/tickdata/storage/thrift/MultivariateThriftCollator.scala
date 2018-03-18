package org.ccfea.tickdata.storage.thrift

//import java.util

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.collector.MarketStateDataCollector
//import org.ccfea.tickdata.thrift.TimeSeriesDatum

//import collection.JavaConversions._
import scala.collection.SortedMap

/**
 * (C) Steve Phelps 2014
 */
trait MultivariateThriftCollator
    extends MarketStateDataCollector[(Option[SimulationTime], SortedMap[String,Option[AnyVal]])] {

  val result: java.util.Map[String, java.util.List[java.lang.Double]] = new java.util.HashMap()

  val timestamps: java.util.List[java.lang.Long] = new java.util.LinkedList[java.lang.Long]()

  def addDatum(variable: String, value: Option[AnyVal]) = {
    val valueList = if (result.containsKey(variable)) {
      result.get(variable)
    } else {
      val emptyList = new java.util.ArrayList[java.lang.Double]()
      result.put(variable, emptyList)
      emptyList
    }
    valueList.add(value match {
      case Some(p:Double) => p
      case Some(l:Long) => l.toDouble
      case _ => Double.NaN
    })
  }

  def outputResult(data: Iterable[(Option[SimulationTime], SortedMap[String, Option[AnyVal]])]) = {
    for ((t, bindings) <- data) {
      for((variable, value) <- bindings) addDatum(variable, value)
      timestamps.add(t.get.getTicks.longValue())
    }
  }

}
