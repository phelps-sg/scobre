package org.ccfea.tickdata.storage.shuffled

import org.ccfea.tickdata.event.OrderReplayEvent
import scala.util.Random

/**
 * (C) Steve Phelps 2014
 */
class RandomWindowedPermutation(source: Iterable[OrderReplayEvent], proportion: Double, val windowSize: Int)
    extends RandomPermutation(source, proportion) {

  override def shuffleTicks(): Unit = {
    tickList.copyToArray(ticks)
    val numWindows = ticks.length / windowSize
    val numShuffledWindows = math.round(proportion * numWindows).toInt
    val windowsToShuffle = sampleWithoutReplacement(numShuffledWindows, numWindows)
    val shuffledPositions = Random.shuffle(windowsToShuffle)
    for(i <- 0 until windowsToShuffle.length) {
      swapWindows(windowsToShuffle(i), shuffledPositions(i))
    }
  }

  def swapWindows(window1: Int, window2: Int) = {
    for(i <- 0 until windowSize) {
      val a = window1 * windowSize + i
      val b = window2 * windowSize + i
      val tmp = ticks(a)
      ticks(a) = ticks(b)
      ticks(b) = tmp
    }
  }

}
