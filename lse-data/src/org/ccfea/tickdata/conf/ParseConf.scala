package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf

/**
 * Command-line arguments parser for parsing-related options
 *
 * (c) Steve Phelps 2013
 */

class ParseConf(args: Seq[String]) extends ScallopConf(args) {
  banner("""Usage: ParseRawData [OPTION]...
           | Parse raw data records from the LSE and insert them into an Apache HBase database.
           |Options:
           |""".stripMargin)
  val bufferSize = opt[Int](default = Some(2000), descr="The number of records to buffer before writing")
  val fileName = opt[String](required=true, descr="The name of the CSV file containing the raw data")
  val recordType = opt[String](required=true, descr="Either order_detail, order_history or trade_report")
}
