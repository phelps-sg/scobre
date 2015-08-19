package org.ccfea.tickdata.storage.shuffled.swapper

import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.storage.shuffled.RandomPermutation

/**
 * Created by sphelps on 23/07/15.
 */
abstract class Swapper[T]{

  def getter(i: Int, ticks: RandomPermutation): T

  def setter(i: Int, x: T, ticks: RandomPermutation): Unit

  def swapAttributes(a: Int, b: Int, ticks: RandomPermutation): Unit = {

    val get: Int => T =
      getter(_, ticks)
    val set: (Int, T) => Unit =
      setter(_, _, ticks)

    val tmp = get(a)
    set(a, get(b))
    set(b, tmp)
  }

}
