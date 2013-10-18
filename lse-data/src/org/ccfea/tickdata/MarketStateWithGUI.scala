package org.ccfea.tickdata

import java.text.SimpleDateFormat
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer
import javax.swing.{SwingUtilities, JLabel, JFrame}
import java.awt.BorderLayout

/**
 * Provides a visualisation of the current-state of the order-book on top of the standard
 * MarketState functionality.
 *
 * (c) Steve Phelps 2013
 */
class MarketStateWithGUI extends MarketState {

  val view: OrderBookView = new OrderBookView(this)

  override def processEvent(ev: Event) = {
    val result = super.processEvent(ev)
    println(ev)
    view.update
    result
  }
}



