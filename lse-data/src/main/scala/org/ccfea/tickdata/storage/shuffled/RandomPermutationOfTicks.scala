package org.ccfea.tickdata.storage.shuffled

import org.ccfea.tickdata.event.TickDataEvent

/**
 * Created by sphelps on 21/07/15.
 */
class RandomPermutationOfTicks(source: Seq[TickDataEvent], proportion: Double, windowSize: Int = 1)
  extends RandomPermutation[TickDataEvent](source, proportion, windowSize,
                                            getter = (i, ticks) => ticks(i),
                                            setter = (i, x, ticks) => ticks(i) = x) {
}
