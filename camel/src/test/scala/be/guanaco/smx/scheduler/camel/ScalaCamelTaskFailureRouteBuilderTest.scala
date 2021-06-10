package be.guanaco.smx.scheduler.camel

import io.guanaco.alerta.impl.AlertaImpl
import be.guanaco.smx.scheduler.Task
import be.guanaco.smx.scheduler.camel.CamelTaskItem.Operation
import org.apache.camel.builder.RouteBuilder
import org.junit.Assert._
import org.junit.Test

/**
  * Test cases for [ScalaCamelTaskItem] - trigger failure
  */
class ScalaCamelTaskFailureRouteBuilderTest extends AbstractCamelSchedulerTest {

  import ScalaCamelTaskFailureRouteBuilderTest._

  var task: Task = _

  @Test
  @throws[InterruptedException]
  def testTriggerFailure() {
    getMockEndpoint(MOCK_HEARTBEATS).expectedMessageCount(0)
    val alerts = getMockEndpoint(MOCK_ALERTS)
    alerts.expectedMessageCount(1)
    assertMockEndpointsSatisfied()
    assertNull(task.getLastRun)

    val alert = alerts.getExchanges.get(0).getIn.getBody(classOf[String])
    assertTrue(alert.contains("\"event\":\"TaskTriggerFailure\""))
  }

  @throws[Exception]
  override protected def createRouteBuilder: RouteBuilder = {
    val alerta = new AlertaImpl(context)
    val builder: CamelTaskRouteBuilder =
      new CamelTaskRouteBuilder("TestTask", "*/5 * * * * ? *", alerta) {
        override val camelTaskItems = Seq(new ScalaCamelTaskItem[String] {
          override def bodies(operation: Operation): Stream[String] =
            throw new RuntimeException("No bodies today!")
          override def endpoints(body: String): Seq[String] =
            Seq(MOCK_TRIGGERED)
        })
      }
    task = builder.getTask
    builder
  }
}

object ScalaCamelTaskFailureRouteBuilderTest {

  val MOCK_TRIGGERED = "mock:triggered"
  val MOCK_HEARTBEATS = "mock:heartbeats"
  val MOCK_ALERTS = "mock:alerts"

}
