package org.ccfea.tickdata

import org.ccfea.tickdata.hbase.{HBaseInserter}
import org.ccfea.tickdata.sql.{SqlInserter, SqlLoader}
import org.ccfea.tickdata.conf.ParseConf
import org.ccfea.tickdata.csv.CsvLoader

class SqlToSqlImport(val batchSize: Int = 2000, val url: String, val driver: String)
    extends SqlLoader with SqlInserter

class SqlToHBaseImport(val batchSize: Int = 20000, val url: String, val driver: String)
    extends SqlLoader with HBaseInserter

class CsvToHbaseImport(val batchSize: Int = 20000, val fileName: String, val recordType: String)
    extends CsvLoader with HBaseInserter

object ParseRawData {

  def main(args: Array[String]) {

    val conf = new ParseConf(args)
    val loader = new CsvToHbaseImport(batchSize = conf.bufferSize(),
                                        fileName = conf.fileName(),
                                          recordType = conf.recordType())
    loader.run
  }
}
