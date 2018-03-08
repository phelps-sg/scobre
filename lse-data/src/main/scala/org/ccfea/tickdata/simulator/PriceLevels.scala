package org.ccfea.tickdata.simulator

import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

import scala.collection.SortedMap
import scalaz.Scalaz._

import net.sourceforge.jasa.market.{Order, OrderBook, Price}

import collection.JavaConverters._

class PriceLevels(val book: OrderBook) {

  def ordersByPrice(orders: Seq[Order]) = orders.groupBy(_.getPrice)

  def priceLevels(orders: Seq[Order]) =
    for ((priceLevel, orders) <- ordersByPrice(orders))
      yield (priceLevel, orders.foldLeft(0L) {
        _ + _.aggregateVolume()
      })

  def askPriceLevels = priceLevels(book.getUnmatchedAsks.asScala)
  def bidPriceLevels = priceLevels(book.getUnmatchedBids.asScala)

  def negatePriceLevels(priceLevels: Map[Price, Long]) =
    (for ((priceLevel, volume) <- priceLevels) yield (priceLevel, -volume))

  val signedPriceLevels = SortedMap[Price, Long]() ++
    (bidPriceLevels |+| negatePriceLevels(askPriceLevels))

  val askVolumeAt = SortedMap[Price, Long]()(Ordering[Price].reverse) ++ askPriceLevels
  val bidVolumeAt = SortedMap[Price, Long]() ++ bidPriceLevels

  val askPrice = List() ++ askVolumeAt.keys
  val bidPrice = List() ++ bidVolumeAt.keys

  def bidVolume(i: Int) = bidVolumeAt(bidPrice(i))
  def askVolume(i: Int) = askVolumeAt(askPrice(i))

  def numAskLevels = askPrice.size
  def numBidLevels = bidPrice.size

}
