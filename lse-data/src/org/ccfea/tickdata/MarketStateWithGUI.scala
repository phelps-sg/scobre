package org.ccfea.tickdata

import grizzled.slf4j.Logger

/**
 * Provides a visualisation of the current-state of the order-book on top of the standard
 * MarketState functionality.
 *
 * (c) Steve Phelps 2013
 */
class MarketStateWithGUI extends MarketState {

  val view: OrderBookView = new OrderBookView(this)

  override val logger = Logger(classOf[MarketStateWithGUI])

  override def processEvent(ev: Event) = {
    val result = super.processEvent(ev)
    logger.info(ev)
    view.update
    result
  }
}



