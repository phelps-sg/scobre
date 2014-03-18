package org.ccfea.tickdata.cep

import org.ccfea.tickdata.simulator.{MarketState, MarketSimulator}
import org.ccfea.tickdata.event.{MarketStateEvent, OrderReplayEvent}
import com.espertech.esper.client._
import org.ccfea.tickdata.event.MarketStateEvent

/**
 * (C) Steve Phelps 2014
 */
class CepMarketSimulator(events: Iterable[OrderReplayEvent], market: MarketState = new MarketState())
    extends MarketSimulator(events, market) with UpdateListener {

  val query = "select avg(state.lastTransactionPrice) from org.ccfea.tickdata.event.MarketStateEvent.win:time(100 sec)"
  val epService = EPServiceProviderManager.getDefaultProvider
  val statement = epService.getEPAdministrator.createEPL(query)
  statement.addListener(this)

  override def process(ev: OrderReplayEvent) = {
    super.process(ev)
    epService.getEPRuntime.sendEvent(new MarketStateEvent(this.market))
  }

  def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit = {
    val ev = newEvents(0)
    System.out.println(ev.getUnderlying.toString)
	  System.out.println(ev.get("avg(state.lastTransactionPrice)"))
  }
}
