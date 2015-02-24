package org.ccfea.tickdata.storage.dao

/**
 * The type of event that has occurred in the exchange.
 *
 * (c) Steve Phelps 2013
 */

object EventType extends Enumeration {
  val None = Value("")
  val OrderSubmitted = Value("order_submitted")
  val OrderRevised = Value("order_revised")
  val Transaction = Value("transaction")
  val OrderDeleted = Value("order_deleted")
  val OrderExpired = Value("order_expired")
  val OrderMatched = Value("order_matched")
  val OrderFilled = Value("order_filled")
  val TransactionLimit = Value("transaction_limit")
}
