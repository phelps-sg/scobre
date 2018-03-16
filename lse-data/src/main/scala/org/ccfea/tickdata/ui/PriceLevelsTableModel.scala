package org.ccfea.tickdata.ui

import javax.swing.table.DefaultTableModel

import org.ccfea.tickdata.simulator.OrderBook

import scala.math.max

class PriceLevelsTableModel(var book:OrderBook) extends DefaultTableModel {

  override def getRowCount: Int = book.size

  override def getColumnName(columnIndex: Int): String =
    columnIndex match {
      case 0 => "Bid Vol"
      case 1 => "Bid Price"
      case 2 => "Ask Price"
      case 3 => "Ask Vol"
    }

  override def getColumnClass(columnIndex: Int): Class[_] = Long.getClass

  override def getColumnCount: Int = 4

  override def setValueAt(aValue: scala.Any, rowIndex: Int, columnIndex: Int): Unit = {}

  override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = false

  override def getValueAt(rowIndex: Int, columnIndex: Int): AnyRef =
    columnIndex match {
      case 0 => if (rowIndex < book.bidPriceLevels.size) book.bidVolume(rowIndex).toString else ""
      case 1 => if (rowIndex < book.bidPriceLevels.size) book.bidPrice(rowIndex).toPrettyString() else ""
      case 2 => if (rowIndex < book.askPriceLevels.size) book.askPrice(rowIndex).toPrettyString() else ""
      case 3 => if (rowIndex < book.askPriceLevels.size) book.askVolume(rowIndex).toString else ""
    }

}
