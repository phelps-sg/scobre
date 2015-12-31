package org.ccfea.tickdata.storage.hbase

import org.apache.hadoop.hbase.client.{HBaseAdmin, Result}
import org.apache.hadoop.hbase.{ HBaseConfiguration, HTableDescriptor }
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.ccfea.tickdata.storage.dao.Event

import org.apache.spark._

object HBaseRead extends HBaseEventConverter {

  def convert(x: Tuple2[ImmutableBytesWritable, Result]) = {
    toEvent(x._2)
  }

  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("HBaseRead").setMaster("local[2]")
    val sc = new SparkContext(sparkConf)
    val conf = HBaseConfiguration.create()
    val tableName = "events"

//    System.setProperty("user.name", "hdfs")
//    System.setProperty("HADOOP_USER_NAME", "hdfs")
    conf.set("hbase.master", "localhost:60000")
    conf.setInt("timeout", 120000)
    conf.set("hbase.zookeeper.quorum", "localhost")
    //conf.set("zookeeper.znode.parent", "/hbase-unsecure")
    conf.set(TableInputFormat.INPUT_TABLE, tableName)

//    val admin = new HBaseAdmin(conf)
//    if (!admin.isTableAvailable(tableName)) {
//      val tableDesc = new HTableDescriptor(tableName)
//      admin.createTable(tableDesc)
//    }
//
    val hBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
    println("Number of Records found : " + hBaseRDD.count())
    val convertedEvents = hBaseRDD.map(convert)
    val firstRecord = convertedEvents.take(1)
    println("First record = " + firstRecord)
    sc.stop()
  }
}

