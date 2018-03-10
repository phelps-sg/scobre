package org.ccfea.tickdata.simulator


import scala.collection.parallel.ParMap
import scala.collection.SortedMap

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

  lazy val askVolumeAt = SortedMap[Price, Long]()(Ordering[Price].reverse) ++ askPriceLevels
  lazy val bidVolumeAt = SortedMap[Price, Long]() ++ bidPriceLevels

  lazy val askPrice = List() ++ askVolumeAt.keys
  lazy val bidPrice = List() ++ bidVolumeAt.keys

  def bidVolume(i: Int) = bidVolumeAt(bidPrice(i))
  def askVolume(i: Int) = askVolumeAt(askPrice(i))

  def numAskLevels = askPrice.size
  def numBidLevels = bidPrice.size

}
