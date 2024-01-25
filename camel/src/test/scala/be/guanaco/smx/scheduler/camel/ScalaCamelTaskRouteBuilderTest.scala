package be.guanaco.smx.scheduler.camel

import io.guanaco.alerta.impl.AlertaImpl
import be.guanaco.smx.scheduler.Task
import be.guanaco.smx.scheduler.camel.CamelTaskItem.Operation
import org.apache.camel.builder.RouteBuilder
import org.junit.Assert._
import org.junit.Test

/**
  * Test cases for [ScalaCamelTaskItem] - trigger successful
  */
class ScalaCamelTaskRouteBuilderTest extends AbstractCamelSchedulerTest {

  import ScalaCamelTaskRouteBuilderTest._

  var task: Task = _

  @Test
  @throws[InterruptedException]
  def testTriggerSucceeds() {
    getMockEndpoint(MOCK_TRIGGERED).expectedBodiesReceived(MESSAGE_1, MESSAGE_2)
    getMockEndpoint(MOCK_HEARTBEATS).expectedMessageCount(1)
    assertMockEndpointsSatisfied()
    assertNotNull(task.getLastRun)
  }

  @throws[Exception]
  override protected def createRouteBuilder: RouteBuilder = {
    val alerta = new AlertaImpl(context)
    val builder: CamelTaskRouteBuilder = new CamelTaskRouteBuilder("TestTask", "*/5 * * * * ? *", alerta) {
      override val camelTaskItems = Seq(new ScalaCamelTaskItem[String] {
        override def bodies(operation: Operation): Stream[String] = Stream(MESSAGE_1, MESSAGE_2)
        override def endpoints(body: String): Seq[String] = Seq(MOCK_TRIGGERED)
      })
    }
    task = builder.getTask
    builder
  }
}

object ScalaCamelTaskRouteBuilderTest {

  val MOCK_TRIGGERED = "mock:triggered"
  val MOCK_HEARTBEATS = "mock:heartbeats"

  val MESSAGE_1 = "First message"
  val MESSAGE_2 = "Second message"

}
