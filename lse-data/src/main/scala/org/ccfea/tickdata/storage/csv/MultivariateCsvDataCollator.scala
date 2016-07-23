package org.ccfea.tickdata.storage.csv

import net.sourceforge.jabm.SimulationTime
import org.ccfea.tickdata.collector.MarketStateDataCollector

/**
 * Created by sphelps on 04/09/14.
 */
trait MultivariateCsvDataCollator
    extends MarketStateDataCollector[(Option[SimulationTime], Map[String,Option[AnyVal]])] with PrintStreamOutputer {

  def outputResult(data: Iterable[(Option[SimulationTime], Map[String, Option[AnyVal]])]) = {
    val out = openOutput()
    for ((t, bindings) <- data) {
      out.print(t.get.getTicks + "\t")
      val n = bindings.size
      var i = 1
      for ((variable, value) <- bindings) {
        out.print(value match {
          case Some(p) => p.toString()
          case None => "NaN"
        })
        if (i < n) out.print('\t') else out.println()
        i = i + 1
      }
    }
    out.close()
    Unit
  }

}
