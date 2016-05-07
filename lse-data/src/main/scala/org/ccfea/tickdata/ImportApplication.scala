package org.ccfea.tickdata

import org.ccfea.tickdata.conf.ParseConf
import org.ccfea.tickdata.storage.DataLoader

/**
 * (C) Steve Phelps 2014
 */
trait ImportApplication extends ScobreApplication {

  def loader(conf: ParseConf): DataLoader

  def main(args: Array[String]) = {
    val conf = new ParseConf(args)
    loader(conf).run()
  }

}
