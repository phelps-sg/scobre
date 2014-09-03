package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf

/**
 * Command-line parser for database-related options.
 *
 * (c) Steve Phelps 2013
 */

class DbConf(args: Seq[String]) extends ScallopConf(args) {
  val url = opt[String]()
  val driver = opt[String](default = Some("com.mysql.jdbc.Driver"))
}
