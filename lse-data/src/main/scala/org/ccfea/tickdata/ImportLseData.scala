package org.ccfea.tickdata

import grizzled.slf4j.Logger
import org.ccfea.tickdata.conf.ParseConf
import org.ccfea.tickdata.storage.hbase.HBaseInserter
import org.ccfea.tickdata.storage.csv.CsvLoader
import org.ccfea.tickdata.storage.rawdata.asx.AsxLoader
import org.ccfea.tickdata.storage.rawdata.lse.LseLoader
import org.ccfea.tickdata.storage.test.TestInserter

/**
 * The main application class for extracting the raw-data supplied by the LSE,
 * transforming it into a canononical chronologically-ordered sequence of event objects,
 * and loading it into a database table.
 *
 * (C) Steve Phelps 2014
 */
object ImportLseData extends ImportApplication {

  class CsvToHbaseImport(val batchSize: Int = 20000, val fileName: String, val recordType: String,
                            override val logger: Logger = Logger("CsvtoHbaseImport"))
      extends CsvLoader with LseLoader with HBaseInserter

  def loader(conf: ParseConf) = new CsvToHbaseImport(batchSize = conf.bufferSize(),
                                        fileName = conf.fileName(),
                                          recordType = conf.recordType())
}
