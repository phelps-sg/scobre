package org.ccfea.tickdata.storage.csv

import java.io.PrintStream

/**
 * (C) Steve Phelps 2014
 */
trait PrintStreamOutputer {

  val outFileName: Option[String]

  def openOutput() = outFileName match {
    case Some(fileName) => {
      new PrintStream(new java.io.FileOutputStream(outFileName.get))
    }
    case None => System.out
  }
}
