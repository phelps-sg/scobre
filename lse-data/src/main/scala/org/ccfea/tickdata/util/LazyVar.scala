package org.ccfea.tickdata.util

class LazyVar[A](val f: () => A) {

  var value: Option[A] = None

  def apply(): A = this.synchronized {
    value match {
      case Some(v) => v
      case _ =>
        val newValue = f()
        value = Some(newValue)
        newValue
    }
  }

  def unvalidate() = this.synchronized { this.value = None }
}
