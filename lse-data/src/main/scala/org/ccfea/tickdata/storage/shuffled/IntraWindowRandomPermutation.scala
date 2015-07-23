package org.ccfea.tickdata.storage.shuffled

import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.storage.shuffled.swapper.Swapper

import scala.util.Random

/**
 * Shuffle ticks within windows.
 *
 * (C) Steve Phelps 2014
 */
class IntraWindowRandomPermutation(source: Seq[TickDataEvent], proportion: Double, windowSize: Int, swapper: Swapper[_])
    extends RandomPermutation(source, proportion, windowSize, swapper) {

  override def shuffleTicks(): Unit = {
    initialise()
    if (proportion > 0.0) {
      val numWindows: Int = ticks.length / windowSize
      for(i <- 0 until numWindows) shuffleWindow(i)
    }
  }

  def shuffleWindow(window: Int): Unit = {
    val numPositionsToShuffle: Int = math.floor(proportion * windowSize).toInt
    val positionsToShuffle = sampleWithoutReplacement(numPositionsToShuffle, windowSize)
    val shuffledPositions = Random.shuffle(positionsToShuffle)
    for(i <- 0 until shuffledPositions.length) {
      val a = positionsToShuffle(i) + window*windowSize
      val b = shuffledPositions(i) + window*windowSize
      swap(a, b)
    }
  }
}
