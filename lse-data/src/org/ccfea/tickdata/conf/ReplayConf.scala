package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf

/**
 * Parser for command-line options related to order replay.
 *
 * (c) Steve Phelps 2013
 */

class ReplayConf(args: Seq[String]) extends ScallopConf(args) {
  val withGui = opt[Boolean](default = Some(false))
  val maxNumEvents = opt[Int]()
  val tiCode = opt[String](required = true)
}

