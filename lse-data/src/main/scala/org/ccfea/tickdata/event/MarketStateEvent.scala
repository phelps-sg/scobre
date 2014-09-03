package org.ccfea.tickdata.event

import org.ccfea.tickdata.simulator.MarketState
import scala.beans.BeanProperty

/**
 * (C) Steve Phelps 2014
 */
case class MarketStateEvent(@BeanProperty state: MarketState) {
  def getStartts = state.time.get.getTicks
  def getEndts = getStartts
}
