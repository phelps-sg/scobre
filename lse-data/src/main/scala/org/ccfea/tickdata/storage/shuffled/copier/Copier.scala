package org.ccfea.tickdata.storage.shuffled.copier

import grizzled.slf4j.Logger
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.storage.shuffled.RandomPermutation

/**
 * Created by sphelps on 23/07/15.
 */
abstract class Copier[T]{

  def getter(i: Int, ticks: RandomPermutation): T

  def setter(i: Int, x: T, ticks: RandomPermutation): Unit

  def copyAttributes(a: Int, b: Int, ticks: RandomPermutation): Unit = {

    val get: Int => T = getter(_, ticks)
    val set: (Int, T) => Unit = setter(_, _, ticks)

    set(a, get(b))
  }

}
