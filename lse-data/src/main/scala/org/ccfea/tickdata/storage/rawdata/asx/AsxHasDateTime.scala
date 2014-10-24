package org.ccfea.tickdata.storage.rawdata.asx

import java.text.SimpleDateFormat

import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * (C) Steve Phelps 2014
 */
trait AsxHasDateTime extends HasDateTime {

  override val df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS")

}
