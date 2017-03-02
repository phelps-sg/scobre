package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf

/**
 * (C) Steve Phelps 2015
 */
class ServerConf(args: Seq[String]) extends ReplayConf(args) {

  version("org.ccfea.tickdata.OrderReplayService" + BuildInfo.version + "b-" + BuildInfo.buildInfoBuildNumber +
            " (c) 2015 Steve Phelps")

  banner("""Usage: OrderReplayService [OPTION]...
           |Start the order-replay server.
           |Options:
           |""".stripMargin)

  footer("\nFor more details, consult the readme.")

  val port = opt[Int](default = Some(9090), descr="The port to listen on")

  verify()
}
