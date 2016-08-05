package org.ccfea.tickdata.storage.csv

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.collector.MarketStateDataCollector

/**
 * Created by sphelps on 04/09/14.
 */
trait MultivariateCsvDataCollator
    extends MarketStateDataCollector[(Option[SimulationTime], Map[String,Option[AnyVal]])] with PrintStreamOutputer {

  val out = openOutput()
  val seperator = '\t'

  def writeRow(variables: Iterable[String]) = {
    val i = variables.iterator
    while (i.hasNext) {
      out.print(i.next())
      if (i.hasNext) out.print(seperator) else out.println()
    }
  }

  def outputResult(data: Iterable[(Option[SimulationTime], Map[String, Option[AnyVal]])]) = {
    val (t, firstRow) = data.iterator.next()
    writeRow(List("t") ++ firstRow.keys)
    for ((t, bindings) <- data) {
      val valueStrings =
        for ((variable, value) <- bindings) yield value match {
          case Some(p) => p.toString()
          case None => "NaN"
        }
      writeRow(List(t.get.getTicks.toString) ++ valueStrings)
    }
    out.close()
    Unit
  }

}
