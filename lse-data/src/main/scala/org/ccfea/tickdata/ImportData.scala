package org.ccfea.tickdata

import grizzled.slf4j.Logger
import org.ccfea.tickdata.conf.ParseConf
import org.ccfea.tickdata.storage.CsvToHBaseLoader
import org.ccfea.tickdata.storage.csv.CsvLoader
import org.ccfea.tickdata.storage.hbase.HBaseInserter
import org.ccfea.tickdata.storage.rawdata.asx.AsxParser
import org.ccfea.tickdata.storage.rawdata.lse.LseParser
import org.springframework.ejb.config.LocalStatelessSessionBeanDefinitionParser

/**
 * (C) Steve Phelps 2014
 */
object ImportData extends ImportApplication {

  def getParser(implicit conf: ParseConf) = {
    conf.parser() match {
      case "ASX" => new AsxParser()
      case "LSE" => new LseParser(conf.recordType())
    }
  }

  def loader(conf: ParseConf) = new CsvToHBaseLoader(parser = getParser(conf),
                                                        batchSize = conf.bufferSize(),
                                                        fileName = conf.fileName())

}
