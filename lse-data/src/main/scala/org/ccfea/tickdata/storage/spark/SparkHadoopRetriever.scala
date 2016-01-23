package org.ccfea.tickdata.storage.spark

import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.ccfea.tickdata.event.TickDataEvent
import org.ccfea.tickdata.storage.dao.Event
import org.ccfea.tickdata.storage.hbase.HBaseEventConverter

class SparkHadoopRetriever extends HBaseEventConverter with Iterable[TickDataEvent] {

  def convertedEvents: RDD[TickDataEvent]= {
    val sparkConf = new SparkConf().setAppName("HBaseRead").setMaster("local[2]")
    val sc = new SparkContext(sparkConf)
    val hBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
    hBaseRDD.map(convert)
  }

  def convert(x: Tuple2[ImmutableBytesWritable, Result]): TickDataEvent = {
    toEvent(x._2).tick
  }

  def iterator: Iterator[TickDataEvent] = convertedEvents.toLocalIterator

}

