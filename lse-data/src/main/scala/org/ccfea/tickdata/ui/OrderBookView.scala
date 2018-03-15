package org.ccfea.tickdata.ui

import java.text.SimpleDateFormat

import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.simulator.MarketState

import scala.collection.mutable.{Publisher, Subscriber}
import scala.swing._

/**
 * A visualisation of the current state of the order book.
 *
 * (c) Steve Phelps 2013
 */
class OrderBookView(val market: MarketState)
    extends Subscriber[TickDataEvent, Publisher[TickDataEvent]] {

  val priceLevelsModel = new PriceLevelsTableModel(market.book)

  val levelsTable = new Table() {
    model = priceLevelsModel
  }

  val df = new SimpleDateFormat("HH:mm:ss:SSSS dd/MM yyyy")

  val timeLabel = new Label()

  val frame = new Frame() {
    title = "Order book"
    contents = new BoxPanel(Orientation.Vertical) {
      contents += timeLabel
      contents += new ScrollPane() {
        contents = levelsTable
      }
    }
    size = new Dimension(640, 480)
  }

  frame.visible = true

  market.subscribe(this)

  def notify(pub: Publisher[TickDataEvent], ev: TickDataEvent) = {
    Swing.onEDTWait {
//      priceLevelsModel.levels = new PriceLevels(market.book)
      timeLabel.text = df.format(new java.util.Date(market.time.get.getTicks))
    }
    priceLevelsModel.fireTableDataChanged()
  }

}


