package org.ccfea.tickdata.simulator

//import javax.swing._
import scala.swing._
import java.text.SimpleDateFormat
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer
import java.awt.BorderLayout

import org.ccfea.tickdata.event.TickDataEvent

import scala.collection.mutable.{Subscriber, Publisher}

/**
 * A visualisation of the current state of the order book.
 *
 * (c) Steve Phelps 2013
 */
class OrderBookView(val market: MarketState)
    extends Subscriber[TickDataEvent, Publisher[TickDataEvent]] {

  market.subscribe(this)

  val priceLevelsModel = new PriceLevelsTableModel(new PriceLevels(market.book))
  val levelsTable = new Table() {
    model = priceLevelsModel
  }

  val df = new SimpleDateFormat("HH:mm:ss:SSSS dd/MM yyyy")
  val timeLabel = new Label()

  val frame = new Frame() {
    title = "Order book"
    contents = new BoxPanel(Orientation.Vertical) {
      contents += timeLabel
      contents += levelsTable
    }
    size = new Dimension(800, 600)
  }
//  frame.pack()
  frame.visible = true

//  val timeLabel = new Label()

  def notify(pub: Publisher[TickDataEvent], ev: TickDataEvent) = {
    priceLevelsModel.levels = new PriceLevels(market.book)
    priceLevelsModel.fireTableDataChanged()
    timeLabel.text = df.format(new java.util.Date(market.time.get.getTicks))
  }

}


