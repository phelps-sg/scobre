package org.ccfea.tickdata.cep

import org.ccfea.tickdata.simulator.{MarketState, MarketSimulator}
import org.ccfea.tickdata.event.{MarketStateEvent, OrderReplayEvent}
import com.espertech.esper.client._
import org.ccfea.tickdata.event.MarketStateEvent
import java.util.{Observable, Observer}
import net.sourceforge.jasa.market.MarketSimulation

/**
 * Dispatch events to the Complex Event Processing (CEP) service.
 *
 * (C) Steve Phelps 2014
 */
class CepObserver extends Observer { // with UpdateListener {

  val query =
    "select avg(state.lastTransactionPrice) from org.ccfea.tickdata.event.MarketStateEvent.win:time(100 sec)"

  val epService = EPServiceProviderManager.getDefaultProvider
//  val statement = epService.getEPAdministrator.createEPL(query)
//  statement.addListener(this)

  def update(o: Observable, arg: scala.Any): Unit = {
    o match {
      case simulator: MarketSimulator =>
        epService.getEPRuntime.sendEvent(new MarketStateEvent(simulator.market))
    }
  }

//  def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit = {
//    val ev = newEvents(0)
//    System.out.println(ev.getUnderlying.toString)
//	  System.out.println(ev.get("avg(state.lastTransactionPrice)"))
//  }

}
