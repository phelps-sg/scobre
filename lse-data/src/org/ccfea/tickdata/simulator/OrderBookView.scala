package org.ccfea.tickdata.simulator

import javax.swing._
import java.text.SimpleDateFormat
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer
import java.awt.BorderLayout

/**
 * A visualisation of the current state of the order book.
 *
 * (c) Steve Phelps 2013
 */
class OrderBookView(val market: MarketState, val maxLevels: Int = 12) {
  //TODO: migrate to scala Swing swing wrappers


  val df = new SimpleDateFormat("HH:mm:ss:SSSS dd/MM yyyy")
  val auctioneer = new ContinuousDoubleAuctioneer()
  auctioneer.setOrderBook(market.book)
  val orderBookView = new net.sourceforge.jasa.view.OrderBookView()
  orderBookView.setAuctioneer(auctioneer)
  orderBookView.setMaxDepth(maxLevels)
  orderBookView.afterPropertiesSet()
  val myFrame = new JFrame()
  val timeLabel = new JLabel()
  myFrame.setLayout(new BorderLayout())
  myFrame.add(orderBookView, BorderLayout.CENTER)
  myFrame.add(timeLabel, BorderLayout.NORTH)
  myFrame.pack()
  myFrame.setVisible(true)

  def update = {
    SwingUtilities.invokeAndWait(new Runnable() {
      def run() = {
        orderBookView.update()
        orderBookView.notifyTableChanged()
        timeLabel.setText(df.format(new java.util.Date(market.time.get.getTicks)))
      }
    })
  }
}

