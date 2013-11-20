package org.ccfea.tickdata.storage.hbase

import org.ccfea.tickdata.storage.hbase.HBaseRetriever
import java.util.Date

/**
 * (c) Steve Phelps 2013
 */
class ConcreteHBaseRetriever(val selectedAsset: String,
                              val startDate: Option[Date], val endDate: Option[Date]) extends HBaseRetriever
