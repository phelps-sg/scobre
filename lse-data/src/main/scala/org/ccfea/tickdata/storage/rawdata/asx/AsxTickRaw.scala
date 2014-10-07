package org.ccfea.tickdata.storage.rawdata.asx

import org.ccfea.tickdata.storage.rawdata.HasDateTime

/**
 * (C) Steve Phelps 2014
 */
case class AsxTickRaw (
  //  Instrument,Date,Time,Record Type,Price,Volume,Bid ID,Ask ID,Bid/Ask,SameOffset,OppOffset,MidOffset
  //  BHP,20070702,10:00:02.921,DELETE,,,10476689054159187521,,B,NA,NA,NA
  messageSequenceNumber: Long,
  assetId: String, date:String, time: String, recordType: String, price:Option[BigDecimal], volume:Option[Long],
  bidId: Option[String], askId: Option[String],
  direction:String,
  sameOffset: Option[BigDecimal], oppOffset: Option[BigDecimal], midOffset:Option[BigDecimal]) extends HasDateTime
