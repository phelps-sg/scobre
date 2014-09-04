package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf

/**
 * Created by sphelps on 04/09/14.
 */
class ServerConf(args: Seq[String]) extends ScallopConf(args) {

  version("org.ccfea.tickdata.OrderReplayService" + BuildInfo.version + " (c) 2014 Steve Phelps")
  banner("""Usage: OrderReplayService [OPTION]...
           |Start the order-replay server.
           |Options:
           |""".stripMargin)
  footer("\nFor more details, consult the readme.")
  val port = opt[Int](default = Some(9090), descr="The port to listen on")
}
