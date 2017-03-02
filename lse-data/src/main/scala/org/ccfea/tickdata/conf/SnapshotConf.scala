package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf

/**
  * Created by sphelps on 27/07/16.
  */
class SnapshotConf (args: Seq[String]) extends ReplayerConf(args) {

  val maxLevels = opt[Int](default = Some(100), descr="Maximum number of price levels to output per tick")

  verify()
}
