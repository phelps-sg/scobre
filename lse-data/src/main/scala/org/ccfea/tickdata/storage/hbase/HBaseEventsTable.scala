package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.mapreduce.TableInputFormat

trait HBaseEventsTable {

  @scala.transient
  val conf = HBaseConfiguration.create()
  conf.set(TableInputFormat.INPUT_TABLE, "events")

  //  val admin = new HBaseAdmin(conf)

  @scala.transient
  val connection = ConnectionFactory.createConnection()

  val eventsTableName = TableName.valueOf("events")

  /**
    * The table containing the time-series of tick events.
    */
  @scala.transient
  val eventsTable = connection.getTable(eventsTableName)


  def closeConnection() = connection.close()

}
