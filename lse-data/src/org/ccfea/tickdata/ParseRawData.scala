package org.ccfea.tickdata

import org.ccfea.tickdata.hbase.{HBaseInserter}
import org.ccfea.tickdata.sql.{SqlInserter, SqlLoader}
import org.ccfea.tickdata.conf.ParseConf

class SqlToSqlImport(val batchSize: Int = 2000, val url: String, val driver: String) extends SqlLoader with SqlInserter
class SqlToHBaseImport(val batchSize: Int = 20000, val url: String, val driver: String) extends SqlLoader with HBaseInserter

object ParseRawData {

  def main(args: Array[String]) {

    val conf = new ParseConf(args)
    val loader = new SqlToHBaseImport(conf.bufferSize(), conf.url(), conf.driver())
    loader.run
  }
}
