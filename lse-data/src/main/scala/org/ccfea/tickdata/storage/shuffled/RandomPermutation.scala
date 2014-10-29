package org.ccfea.tickdata.storage.shuffled

import org.ccfea.tickdata.event.OrderReplayEvent
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * (C) Steve Phelps 2014
 */
class RandomPermutation(source: Iterable[OrderReplayEvent], var proportion: Double, var windowSize: Int = 1)
      extends Iterable[OrderReplayEvent] {

  val tickList = source.iterator.toList
  var ticks: Array[OrderReplayEvent] = new Array[OrderReplayEvent](tickList.length)

  override def iterator: Iterator[OrderReplayEvent] = {
    shuffleTicks()
    return ticks.iterator
  }

  def shuffleTicks(): Unit = {
    tickList.copyToArray(ticks)
    if (proportion > 0.0) {
      val numWindows = ticks.length / windowSize
      val numShuffledWindows = math.floor(proportion * numWindows).toInt
      val windowsToShuffle = sampleWithoutReplacement(numShuffledWindows, numWindows)
      val shuffledPositions = Random.shuffle(windowsToShuffle)
      for(i <- 0 until windowsToShuffle.length) {
        swapWindows(windowsToShuffle(i), shuffledPositions(i))
      }
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

  def sampleWithoutReplacement(n: Int, N: Int): Seq[Int] = {
    var t: Int = 0
    var m: Int = 0

    val samples = new ListBuffer[Int]()

    while (m < n) {

      if ((N - t) * Random.nextDouble() >= n - m) {
        t = t + 1
      } else {
        samples += t
        t = t + 1
        m = m + 1
      }
    }
    samples
  }

}
