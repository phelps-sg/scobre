package org.ccfea.tickdata.conf

import org.rogach.scallop.ScallopConf

/**
 * Command-line arguments parser for parsing-related options
 *
 * (c) Steve Phelps 2015
 */

class ParseConf(args: Seq[String]) extends ScallopConf(args) {
  banner("""Usage: ParseRawData [OPTION]...
           | Parse raw data records from the LSE and insert them into an Apache HBase database.
           |Options:
           |""".stripMargin)
  val bufferSize = opt[Int](default = Some(2000), descr="The number of records to buffer before writing")
  val fileName = opt[String](required=true, descr="The name of the CSV file containing the raw data")
  val parser = opt[String](required=false, default = Some("LSE"), descr="The format of the data being imported; either LSE or ASX")
  val recordType = opt[String](required=false, descr="For LSE data: one of order_detail, order_history or trade_report")
}
