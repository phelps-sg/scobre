package org.ccfea.tickdata.storage.shuffled.copier

import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.storage.shuffled.RandomPermutation

/**
 * Created by sphelps on 23/07/15.
 */
class TickCopier extends Copier[TickDataEvent] {

  def getter(i: Int, ticks: RandomPermutation) = ticks(i)

  def setter(i: Int, x: TickDataEvent, ticks: RandomPermutation) = ticks(i) = x

}
