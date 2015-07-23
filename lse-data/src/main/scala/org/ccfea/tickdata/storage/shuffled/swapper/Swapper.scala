package org.ccfea.tickdata.storage.shuffled.swapper

import org.ccfea.tickdata.event.TickDataEvent

/**
 * Created by sphelps on 23/07/15.
 */
abstract class Swapper[T]{

  def getter(i: Int, ticks: Array[TickDataEvent]): T

  def setter(i: Int, x: T, ticks: Array[TickDataEvent]): Unit

  def swapAttributes(a: Int, b: Int, ticks: Array[TickDataEvent]): Unit = {

    val get: Int => T =
      getter(_, ticks)
    val set: (Int, T) => Unit =
      setter(_, _, ticks)

    val tmp = get(a)
    set(a, get(b))
    set(b, tmp)
  }

}
