package org.ccfea.tickdata.ui

import javax.swing.table.DefaultTableModel

import org.ccfea.tickdata.simulator.PriceLevels

import scala.math.max

class PriceLevelsTableModel(var levels:PriceLevels) extends DefaultTableModel {

  override def getRowCount: Int = max(levels.numAskLevels, levels.numBidLevels)

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
      case 0 => levels.bidVolume(rowIndex).toString
      case 1 => levels.bidPrice(rowIndex).toPrettyString()
      case 2 => levels.askPrice(rowIndex).toPrettyString()
      case 3 => levels.askVolume(rowIndex).toString
    }

}
