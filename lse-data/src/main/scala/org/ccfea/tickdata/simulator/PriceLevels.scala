package org.ccfea.tickdata.simulator

import net.sourceforge.jasa.market.{Order, Price}
import org.ccfea.tickdata.util.LazyVar

import scala.collection.mutable

class PriceLevels(implicit val ordering: Ordering[Price]) {

  val levels = new mutable.TreeMap[Price, Long]()(ordering)

  val prices: LazyVar[List[Price]] = new LazyVar[List[Price]](() => List[Price]() ++ levels.keys)

  def size: Int = levels.size

  def apply(p: Price): Long = {
    levels(p)
  }

  def negatedVolume = for ((priceLevel, volume) <- levels) yield (priceLevel, -volume)

  def increment(price: Price, volDelta: Long): Unit = {
    if (!levels.contains(price)) levels(price) = 0
    levels(price) = levels(price) + volDelta
    assert(levels(price) >= 0)
    if (levels(price) == 0) {
      levels.remove(price)
    }
    update()
  }

  def update(): Unit = {
    prices.unvalidate()
  }

}
