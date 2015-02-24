package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf
import java.util.Date

/**
 * Parser for command-line options related to order replay.
 *
 * (c) Steve Phelps 2015
 */
class ReplayConf(args: Seq[String]) extends ScallopConf(args) {

  version("org.ccfea.tickdata.OrderReplay " + BuildInfo.version + "-b" + BuildInfo.buildinfoBuildnumber
             + " (c) 2015 Steve Phelps")
  val explicitClearing = opt[Boolean](default = Some(false), descr="Perform uncrossing explicitly by processing OrderMatchedEvent and OrderFilledEvent")
}

