package org.ccfea.tickdata.storage

import org.ccfea.tickdata.storage.csv.CsvLoader
import org.ccfea.tickdata.storage.hbase.HBaseInserter

/**
 * (C) Steve Phelps 2014
 */
class CsvToHBaseLoader(val parser: DataParser, val batchSize: Int = 20000, val fileName: String)
  extends CsvLoader with HBaseInserter
