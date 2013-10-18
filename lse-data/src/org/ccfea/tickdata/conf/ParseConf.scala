package org.ccfea.tickdata.conf

/**
 * Command-line arguments parser for parsing-related options
 *
 * (c) Steve Phelps 2013
 */

class ParseConf(args: Seq[String]) extends DbConf(args) {
  val bufferSize = opt[Int](default = Some(2000))
}
