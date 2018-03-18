package org.ccfea.tickdata

import org.ccfea.tickdata.conf.ReplayerConf

/**
 * The main application for running order-book reconstruction simulations.
 *
 * (C) Steve Phelps 2016
 */
object ReplayOrders {

  def main(args: Array[String]) {
    val conf = new ReplayerConf(args)
    val app = new ReplayApplication(conf)
    app.run()
  }

}
