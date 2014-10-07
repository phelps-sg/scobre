package org.ccfea.tickdata.storage.rawdata.asx

import org.ccfea.tickdata.event.{EventType, Event}
import org.ccfea.tickdata.order.{TradeDirection, MarketMechanismType}
import org.ccfea.tickdata.storage.DataLoader
import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * (C) Steve Phelps 2014
 */
trait AsxLoader extends DataLoader {
//  Instrument,Date,Time,Record Type,Price,Volume,Bid ID,Ask ID,Bid/Ask,SameOffset,OppOffset,MidOffset
//  BHP,20070702,10:00:02.921,DELETE,,,10476689054159187521,,B,NA,NA,NA

  def toRecord(values: Array[Option[String]], lineNumber: Long): HasDateTime = {

    var i = 0
    def next: Option[String] = {
      val result = values(i)
      i = i+1
      result
    }

    new AsxTickRaw(lineNumber, next.get, next.get, next.get, next.get, next, next, next, next, next.get, next, next, next)
  }


  def parseEvent(rawEvent: HasDateTime): Event = {

    implicit def toTradeDirection(buySellInd: String) = {
      buySellInd match {
        case "B" => TradeDirection.Buy
        case "A" => TradeDirection.Sell
      }
    }
    implicit def recordTypeToOrderAction(recordType: String) = {
      recordType match {
        case "DELETE" => EventType.OrderDeleted
        case "AMEND" => EventType.OrderRevised
        case "ENTER" => EventType.OrderSubmitted
      }
    }

    rawEvent match {
      case ev: AsxTickRaw =>
        Event(None, ev.recordType, ev.messageSequenceNumber,
          ev.timeStamp, ev.assetId, "ASX", "AUD",
          if (ev.recordType == EventType.OrderSubmitted) Some(MarketMechanismType.LimitOrder) else None,
          ev.volume, Some(ev.direction),
          ev.bidId, ev.volume, None, None, None, ev.price, None,
          ev.askId, None,
          None, None, None)

    }
  }
}
