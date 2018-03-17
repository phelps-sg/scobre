package org.ccfea.tickdata

import grizzled.slf4j.Logger
import org.apache.thrift.server.TThreadPoolServer
import org.apache.thrift.transport.TServerSocket
import org.ccfea.tickdata.conf.{BuildInfo, ServerConf}
import org.ccfea.tickdata.thrift.OrderReplay

object StartOrderReplayService {

  def main(args: Array[String]): Unit = {

    implicit val conf = new ServerConf(args)
    val port: Int = conf.port()

    val logger = Logger("org.ccfea.tickdata.StartOrderReplayService")

    val processor = new OrderReplay.Processor(new OrderReplayService(conf))
    val serverTransport = new TServerSocket(port)
    val server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor))

    logger.info("SCOBRE order-replay server version " + BuildInfo.version)
    logger.info("Server running on port " + port + "... ")
    server.serve()
    logger.info("Server terminated.")
  }
}
