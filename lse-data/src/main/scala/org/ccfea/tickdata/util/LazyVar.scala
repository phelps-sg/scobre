package org.ccfea.tickdata.util

class LazyVar[A](val f: () => A) {

  var value: Option[A] = None

  def apply(): A = {
    value match {
      case Some(v) => v
      case _ =>
        val newValue = f()
        value = Some(newValue)
        newValue
    }
  }

  def unvalidate() = { this.value = None }
}
