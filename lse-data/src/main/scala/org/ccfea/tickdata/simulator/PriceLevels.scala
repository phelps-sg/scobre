package org.ccfea.tickdata.simulator

import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

import scala.collection.parallel.ParMap
import scala.collection.{SortedMap, mutable}
//import scalaz.Scalaz._

import net.sourceforge.jasa.market.{Order, OrderBook, Price}

import collection.JavaConverters._

class PriceLevels(val book: OrderBook) {

  def ordersByPrice(orders: Seq[Order]) = orders.par.groupBy(_.getPrice)

  def priceLevels(orders: Seq[Order]) =
    for ((priceLevel, orders) <- ordersByPrice(orders))
      yield (priceLevel, orders.foldLeft(0L) {
        _ + _.aggregateVolume()
      })

  def askPriceLevels = priceLevels(book.getUnmatchedAsks.asScala)
  def bidPriceLevels = priceLevels(book.getUnmatchedBids.asScala)

  def negatedVolume(levels: ParMap[Price, Long]) =
    for ((priceLevel, volume) <- levels) yield (priceLevel, -volume)

  def signedPriceLevels =
      SortedMap[Price, Long]() ++ bidPriceLevels ++ negatedVolume(askPriceLevels)

  val askVolumeAt = SortedMap[Price, Long]()(Ordering[Price].reverse) ++ askPriceLevels
  val bidVolumeAt = SortedMap[Price, Long]() ++ bidPriceLevels

  val askPrice = List() ++ askVolumeAt.keys
  val bidPrice = List() ++ bidVolumeAt.keys

  def bidVolume(i: Int) = bidVolumeAt(bidPrice(i))
  def askVolume(i: Int) = askVolumeAt(askPrice(i))

  def numAskLevels = askPrice.size
  def numBidLevels = bidPrice.size

}
