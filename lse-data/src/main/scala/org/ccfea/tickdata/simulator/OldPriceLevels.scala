package org.ccfea.tickdata.simulator


import scala.collection.parallel.ParMap
import scala.collection.{SortedMap, mutable}
import net.sourceforge.jasa.market.{Order, OrderBook, Price}

import collection.JavaConverters._

class OldPriceLevels(val book: OrderBook) {

  def ordersByPrice(orders: mutable.Set[Order]) = orders.par.groupBy(_.getPrice)

  def priceLevels(orders: mutable.Set[Order]) =
    for ((priceLevel, orders) <- ordersByPrice(orders))
      yield (priceLevel, orders.foldLeft(0L) {
        _ + _.aggregateVolume()
      })

  def askPriceLevels = priceLevels(book.asks)
  def bidPriceLevels = priceLevels(book.bids)

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
