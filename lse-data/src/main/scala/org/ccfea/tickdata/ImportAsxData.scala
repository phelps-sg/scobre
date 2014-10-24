package org.ccfea.tickdata

import grizzled.slf4j.Logger
import org.ccfea.tickdata.conf.ParseConf
import org.ccfea.tickdata.storage.csv.CsvLoader
import org.ccfea.tickdata.storage.hbase.HBaseInserter
import org.ccfea.tickdata.storage.rawdata.asx.AsxLoader

/**
 * (C) Steve Phelps 2014
 */
object ImportAsxData extends ImportApplication {

  class AsxCsvToHbaseImport(val batchSize: Int = 20000, val fileName: String,
                            override val logger: Logger = Logger("CsvtoHbaseImport"))
      extends CsvLoader with AsxLoader with HBaseInserter

  def loader(conf: ParseConf) = new AsxCsvToHbaseImport(batchSize = conf.bufferSize(),
                                                        fileName = conf.fileName())

}
