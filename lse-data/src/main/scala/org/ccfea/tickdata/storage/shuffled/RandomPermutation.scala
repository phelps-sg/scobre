package org.ccfea.tickdata.storage.shuffled

import org.ccfea.tickdata.event.OrderReplayEvent

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * (C) Steve Phelps 2014
 */
class RandomPermutation(val source: Iterable[OrderReplayEvent], val proportion: Double)
    extends Iterable[OrderReplayEvent] {

  val tickList = source.iterator.toList
  var ticks: Array[OrderReplayEvent] = new Array[OrderReplayEvent](tickList.length)

  override def iterator: Iterator[OrderReplayEvent] = {
    shuffleTicks()
    return ticks.iterator
  }

  def shuffleTicks() = {
    tickList.copyToArray(ticks)
    val n = math.round(ticks.length.toDouble * proportion).toInt
    val positions: Seq[Int] = sampleWithoutReplacement(n, ticks.length)
    val shuffledPositions = Random.shuffle(positions)
    for(i <- 0 until positions.length) {
      val a = positions(i)
      val b = shuffledPositions(i)
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
