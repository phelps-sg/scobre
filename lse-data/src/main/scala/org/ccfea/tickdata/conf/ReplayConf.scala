package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf
import java.util.Date

/**
 * Parser for command-line options related to order replay.
 *
 * (c) Steve Phelps 2014
 */
class ReplayConf(args: Seq[String]) extends ScallopConf(args) {

  version("org.ccfea.tickdata.OrderReplay " + BuildInfo.version + "-b" + BuildInfo.buildinfoBuildnumber
             + " (c) 2014 Steve Phelps")
  val implicitClearing = opt[Boolean](default = Some(false), descr="Perform uncrossing explicitly by processing OrderMatchedEvent and OrderFilledEvent")
}

