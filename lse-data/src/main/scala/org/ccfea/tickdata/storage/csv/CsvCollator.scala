package org.ccfea.tickdata.storage.csv

import java.io.PrintStream

import org.ccfea.tickdata.simulator.MarketStateDataCollector
import net.sourceforge.jabm.SimulationTime

/**
 * Collate data collected from the order-book reconstruction to a CSV file.
 *
 * (C) Steve Phelps 2014
 */
trait CsvCollator extends MarketStateDataCollector[(Option[SimulationTime], Option[AnyVal])] with PrintStreamOutputer {

  /**
   * Write the collected time-series to a CSV file separated by tabs.  Each row of the file
   * corresponds to a different measurement.  The first column is the time value
   * and the second column is the value of the variable that is being collected.
   * A value of None is translated as NaN.
   *
   * @param data  An Iterable over the data we have collected through the dataCollector.
   */
  def outputResult(data: Iterable[(Option[SimulationTime], Option[AnyVal])]) = {
    val out = openOutput()
    for ((t, price) <- data) {
      out.println(t.get.getTicks + "\t" + (price match {
        case Some(p) => p.toString()
        case None => "NaN"
      }))
    }
    out.close()
    Unit
  }

}
