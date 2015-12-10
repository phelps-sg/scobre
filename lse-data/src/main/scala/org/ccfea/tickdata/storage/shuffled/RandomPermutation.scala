package org.ccfea.tickdata.storage.shuffled

import org.ccfea.tickdata.event.{OrderSubmittedEvent, TickDataEvent}
import org.ccfea.tickdata.storage.rawdata.HasDateTime
import org.ccfea.tickdata.storage.shuffled.copier.{Copier, TickCopier}
import scala.util.Random
import scala.collection.mutable.Map

/**
 * A random permutation of tick objects.
 *
 * (C) Steve Phelps 2014
 */
class RandomPermutation(val source: Seq[TickDataEvent], val proportion: Double, val windowSize: Int = 1,
                         val copier: Copier[_] = new TickCopier())
      extends Seq[TickDataEvent] {

  val n: Int = source.length - (source.length % windowSize)
  val ticks: Array[TickDataEvent] = new Array[TickDataEvent](n)
  val shuffledTicks: Array[TickDataEvent] = new Array[TickDataEvent](n)

  /**
   * A map from order-codes to the index of the corresponding OrderSubmittedEvent where
   * that order-code was first entered.
   */
  val orderCodeMap = Map[String, Integer]()

  shuffleTicks()

  def initialise(): Unit = {
    source.copyToArray(ticks, 0, n)
    source.copyToArray(shuffledTicks, 0, n)
    for(i <- 0 until n) {
      val ev = ticks(i)
      ev match {
        case os: OrderSubmittedEvent =>
          orderCodeMap(os.order.orderCode) = i
        case _ =>
          // No action
      }
    }
  }

  def shuffleTicks(): Unit = {
    initialise()
    if (proportion > 0.0) {
      val numWindows = ticks.length / windowSize
      val numShuffledWindows = math.floor(proportion * numWindows).toInt
      val windowsToShuffle = sampleWithoutReplacement(numShuffledWindows, numWindows)
      val shuffledPositions = Random.shuffle(windowsToShuffle)
      windowsToShuffle.indices.par.map(
        i => copyWindows(windowsToShuffle(i), shuffledPositions(i))
      )
//      for(i <- 0 until windowsToShuffle.length) {
//        copyWindows(windowsToShuffle(i), shuffledPositions(i))
//      }
     }
  }

  def copyWindows(window1: Int, window2: Int) = {
    for(i <- 0 until windowSize) {
      val a = window1 * windowSize + i
      val b = window2 * windowSize + i
      copy(a, b)
    }
  }

  def copy(a: Int, b: Int) = copier.copyAttributes(a, b, this)

  def sampleWithoutReplacement(n: Int, N: Int): Seq[Int] = {
    var t: Int = 0
    var m: Int = 0
    val samples = new Array[Int](n)
    while (m < n) {
      if ((N - t) * Random.nextDouble() >= n - m) {
        t = t + 1
      } else {
        samples(m) = t
        t = t + 1
        m = m + 1
      }
    }
    samples
  }

  override def iterator: Iterator[TickDataEvent] = shuffledTicks.iterator

  override def length: Int = ticks.length

  override def apply(i: Int): TickDataEvent = ticks(i)

  def apply(orderCode: String): Option[TickDataEvent] =
    if (orderCodeMap.contains(orderCode)) Some(ticks(orderCodeMap(orderCode))) else None

  def update(i: Int, x: TickDataEvent) = shuffledTicks(i) = x

  def update(orderCode: String, x: TickDataEvent) = shuffledTicks(orderCodeMap(orderCode)) = x
}
