package org.ccfea.tickdata.storage.rawdata.asx

import org.ccfea.tickdata.order.{TradeDirection, MarketMechanismType}
import org.ccfea.tickdata.storage.dao.{Event, EventType}
import org.ccfea.tickdata.storage.{DataParser, DataLoader}
import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * (C) Steve Phelps 2014
 */
class AsxParser extends DataParser {

  def toRecord(values: Array[Option[String]], lineNumber: Long): HasDateTime = {
    //  Instrument,Date,Time,Record Type,Price,Volume,Bid ID,Ask ID,Bid/Ask,SameOffset,OppOffset,MidOffset
    //  BHP,20070702,10:00:02.921,DELETE,,,10476689054159187521,,B,NA,NA,NA
    var i = 0
    def next: Option[String] = {
      val result = values(i)
      i = i + 1
      result
    }
    new AsxTickRaw(lineNumber, next.get, next.get, next.get, next.get, next, next, next, next, next.get, next, next, next)
  }

  def parseEvent(rawEvent: HasDateTime): Event = {

    implicit def toTradeDirection(buySellInd: String): TradeDirection.Value = {
      buySellInd match {
        case "B" => TradeDirection.Buy
        case "A" => TradeDirection.Sell
      }
    }

    implicit def recordTypeToOrderAction(recordType: String): EventType.Value = {
      recordType match {
        case "DELETE" => EventType.OrderDeleted
        case "AMEND" => EventType.OrderRevised
        case "ENTER" => EventType.OrderSubmitted
      }
    }

    rawEvent match {
      case ev: AsxTickRaw =>
        Event(None, ev.recordType, ev.messageSequenceNumber, ev.timeStamp, ev.assetId, "ASX", "AUD",
                            Some(MarketMechanismType.LimitOrder),  ev.volume, Some(ev.direction),
                            if (ev.direction == "B") ev.bidId else ev.askId, ev.volume,
                            None, None, None,
                            ev.price,
                            None, ev.askId, None, None, None, None)
    }
  }

}
