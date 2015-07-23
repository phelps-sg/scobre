package org.ccfea.tickdata.storage.shuffled.swapper

import org.ccfea.tickdata.event.TickDataEvent

/**
 * Created by sphelps on 23/07/15.
 */
class TickSwapper extends Swapper[TickDataEvent] {

  def getter(i: Int, ticks: Array[TickDataEvent]) = ticks(i)

  def setter(i: Int, x: TickDataEvent, ticks: Array[TickDataEvent]) = ticks(i) = x

}
