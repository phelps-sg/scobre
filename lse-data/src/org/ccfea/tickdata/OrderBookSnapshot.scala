package org.ccfea.tickdata

import org.ccfea.tickdata.conf.ReplayConf
import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import org.ccfea.tickdata.simulator._

import java.text.DateFormat
import java.util.{Calendar, GregorianCalendar, Date}

import grizzled.slf4j.Logger

import net.sourceforge.jabm.SimulationTime

import scala.Some

object OrderBookSnapshot {

  val logger = Logger("org.ccfea.tickdata.OrderReplay")

  def parseDate(date: Option[String]): Option[Date] = date match {
    case None => None
    case Some(dateStr) =>  Some(DateFormat.getDateInstance(DateFormat.SHORT).parse(dateStr))
  }

  def main(args: Array[String]) {

    val conf = new ReplayConf(args)

    val time = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(conf.startDate.get.get)
    println(time)

    val startCal = new GregorianCalendar()
    startCal.setTime(time)
    startCal.set(Calendar.HOUR, 0)
    startCal.set(Calendar.MINUTE, 0)
    startCal.set(Calendar.SECOND, 0)
    val start = Some(startCal.getTime)

    val endCal = new GregorianCalendar()
    endCal.setTime(time)
    endCal.add(Calendar.MINUTE, 5)
    val end = Some(endCal.getTime)

    val eventSource =
      new HBaseRetriever(selectedAsset = conf.tiCode(), startDate = start, endDate = end)

    val snapShotter =
      new OrderBookSnapshotter(eventSource, new SimulationTime(time.getTime), conf.outFileName.get, conf.withGui())
    snapShotter.run
  }
}

