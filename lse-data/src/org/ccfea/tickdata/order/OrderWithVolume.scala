package org.ccfea.tickdata.order

/**
 * (C) Steve Phelps 2013
 */
abstract class OrderWithVolume extends AbstractOrder {
  def orderCode: String
  def aggregateSize: Long
  def tradeDirection: TradeDirection.Value
}
