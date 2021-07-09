package io.guanaco.scheduler.camel

import CamelTaskItem.Operation
import org.apache.camel.ProducerTemplate

/**
  * Created by gertv on 3/1/17.
  */
trait ScalaCamelTaskItem[T] extends CamelTaskItem {

  def bodies(operation: Operation): Stream[T]

  def endpoints(body: T): Seq[String]

  override def handle(template: ProducerTemplate, operation: Operation): Unit = {
    bodies(operation) foreach { body =>
      endpoints(body) foreach { endpoint =>
        template.sendBody(endpoint, body)
      }
    }
  }
}
