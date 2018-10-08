package be.guanaco.smx.scheduler.camel

import be.guanaco.smx.scheduler.camel.CamelTaskItem.Operation
import org.apache.camel.ProducerTemplate

/**
  * Created by gertv on 3/1/17.
  */
trait CamelTaskItem {

  def handle(template: ProducerTemplate, operation: Operation)

}

object CamelTaskItem {

  trait Operation
  case class Full() extends Operation
  case class Incremental() extends Operation
  case class Subset(selector: String) extends Operation

}