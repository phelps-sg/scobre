package org.ccfea.tickdata

import grizzled.slf4j.Logger
import org.ccfea.tickdata.event.{OrderReplayEvent}

/**
 * Provides a visualisation of the current-state of the order-book on top of the standard
 * MarketState functionality.
 *
 * (c) Steve Phelps 2013
 */
class MarketStateWithGUI extends MarketState {

  val view: OrderBookView = new OrderBookView(this)

  override val logger = Logger(classOf[MarketStateWithGUI])

  override def newEvent(ev: OrderReplayEvent) = {
    val result = super.newEvent(ev)
    logger.info(ev)
    view.update
    result
  }
}



