package org.ccfea.tickdata.simulator

import net.sourceforge.jasa.market.{Order, Price}

import scala.collection.mutable

class PriceLevels(implicit val ordering: Ordering[Price]) {

  val levels = new mutable.TreeMap[Price, Long]()(ordering)

  def increment(price: Price, volDelta: Long): Unit = {
    if (!levels.contains(price)) levels(price) = 0
    levels(price) = levels(price) + volDelta
  }

  def size: Int = levels.size

  def apply(p: Price): Long = {
    levels(p)
  }

  def prices: Seq[Price] = {
    List[Price]() ++ levels.keys
  }

}
