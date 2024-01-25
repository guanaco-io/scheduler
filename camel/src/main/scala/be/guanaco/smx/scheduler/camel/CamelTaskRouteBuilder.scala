package be.guanaco.smx.scheduler.camel

import java.time.LocalDateTime

import io.guanaco.alerta.api.{Alert, Alerta, Heartbeat}
import be.guanaco.smx.scheduler.camel.CamelTaskItem.{Full, Incremental, Operation, Subset}
import be.guanaco.smx.scheduler.{Task, TaskStatus}
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.{Body, Handler, ProducerTemplate}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Abstract Camel [[RouteBuilder]] implementation to implement an [[Task]] using Camel routes.
  * It also provides out-of-the-box integration with Alerta (sending heartbeats for every run of the task).
  */
abstract class CamelTaskRouteBuilder(identifier: String, schedule: String, period: Option[Long], alerta: Alerta) extends RouteBuilder {

  // convenience constructor for the Java RouteBuilder
  def this(identifier: String, schedule: String, alerta: Alerta) = this(identifier, schedule, None, alerta)

  import CamelTaskRouteBuilder._

  val task = new CamelTask(identifier, schedule)
  val endpoint = s"seda://trigger.for.$identifier"

  val camelTaskItems: Seq[CamelTaskItem]

  /**
    * Get the task instance that is being implemented by this route
    *
    * @return the task
    */
  def getTask: Task = task

  @throws[Exception]
  override def configure(): Unit = {
    val helper = CamelTaskRouteBuilderHelper()

    from(s"quartz:$identifier?cron=$schedule")
      .setBody(constant(Full()))
      .to(endpoint)

    period map { value =>
      from(s"timer:$identifier?period=$value")
        .setBody(constant(Incremental()))
        .to(endpoint)
    }

    from(endpoint)
      .bean(helper)
  }

  /**
    * Inner helper class to handle the actual camel task items
    */
  case class CamelTaskRouteBuilderHelper() {

    lazy val template: ProducerTemplate = getContext.createProducerTemplate()

    @Handler
    def handleCamelTasks(@Body operation: Operation) {
      try {
        task.status = TaskStatus.ACTIVE
        for (cti <- camelTaskItems) {
          cti.handle(template, operation)
        }
        task.lastRun = LocalDateTime.now
        val heartbeat = Heartbeat(task.getIdentifier, Seq(), TimeOut)
        alerta.sendHeartbeat(heartbeat)
        alerta.sendAlert(createAlert(TaskTriggerSuccess).withSeverity("normal"))
      } catch {
        case e: Exception =>
          alerta.sendAlert(
            createAlert(TaskTriggerFailure).withValue(e.getClass.getSimpleName).withText(e.getMessage)
          )
      } finally {
        task.status = TaskStatus.IDLE
      }
    }

    private def createAlert(event: TaskEvent): Alert = {
      val correlates = Seq(TaskTriggerFailure, TaskTriggerSuccess)
      Alert(s"scheduler:${task.identifier}", event, Array("servicemix", "scheduler"), correlate = Some(correlates))
    }

    type TaskEvent = String
    val TaskTriggerFailure: TaskEvent = "TaskTriggerFailure"
    val TaskTriggerSuccess: TaskEvent = "TaskTriggerSuccess"
  }

  /**
    * Inner class to represent the [Task] corresponding to these routes
    */
  class CamelTask(val identifier: String, val schedule: String) extends Task {

    lazy val template: ProducerTemplate = getContext.createProducerTemplate()

    var status: TaskStatus = TaskStatus.IDLE
    var lastRun: LocalDateTime = _

    override def getSchedule: String = schedule

    override def getIdentifier: String = identifier

    override def getLastRun: LocalDateTime = lastRun

    override def trigger(): Unit = template.sendBody(endpoint, Full())

    override def trigger(selector: String): Unit = template.sendBody(endpoint, Subset(selector))

    def getStatus: TaskStatus = status
  }

}

object CamelTaskRouteBuilder {

  // default timeout of 30 hours
  val TimeOut: Int = 30 * 60 * 60

  // logger
  val Log: Logger = LoggerFactory.getLogger(classOf[CamelTaskRouteBuilder])

}

