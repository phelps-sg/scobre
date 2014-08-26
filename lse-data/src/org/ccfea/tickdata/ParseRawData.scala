package org.ccfea.tickdata

import org.ccfea.tickdata.conf.ParseConf
import org.ccfea.tickdata.storage.hbase.HBaseInserter
import org.ccfea.tickdata.storage.csv.CsvLoader

/**
 * The main application class for extracting the raw-data supplied by the LSE,
 * transforming it into a canononical chronologically-ordered sequence of event objects,
 * and loading it into a database table.
 *
 * (C) Steve Phelps 2014
 */
object ParseRawData {

  class CsvToHbaseImport(val batchSize: Int = 20000, val fileName: String, val recordType: String)
      extends CsvLoader with HBaseInserter

//  class SqlToSqlImport(val batchSize: Int = 2000, val url: String, val driver: String)
//      extends SqlLoader with SqlInserter
//
//  class SqlToHBaseImport(val batchSize: Int = 20000, val url: String, val driver: String)
//      extends SqlLoader with HBaseInserter

  def main(args: Array[String]) {

    val conf = new ParseConf(args)
    val loader = new CsvToHbaseImport(batchSize = conf.bufferSize(),
                                        fileName = conf.fileName(),
                                          recordType = conf.recordType())
    loader.run
  }
}
