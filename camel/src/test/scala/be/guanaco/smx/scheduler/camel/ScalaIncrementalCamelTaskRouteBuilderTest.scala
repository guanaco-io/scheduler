package be.guanaco.smx.scheduler.camel

import io.guanaco.alerta.impl.AlertaImpl
import be.guanaco.smx.scheduler.Task
import be.guanaco.smx.scheduler.camel.CamelTaskItem.{ Full, Incremental, Operation }
import org.apache.camel.builder.RouteBuilder
import org.junit.Assert._
import org.junit.Test

/**
  * Test cases for incremental task support on [ScalaCamelTaskItem]
  */
class ScalaIncrementalCamelTaskRouteBuilderTest extends AbstractCamelSchedulerTest {

  import AbstractCamelSchedulerTest._
  import ScalaIncrementalCamelTaskRouteBuilderTest._

  var task: Task = _

  @Test
  @throws[InterruptedException]
  def testTriggerSucceeds() {
    getMockEndpoint(MOCK_TRIGGERED).expectedBodiesReceived(MESSAGE_1, MESSAGE_2, MESSAGE_3)
    getMockEndpoint(MOCK_HEARTBEATS).expectedMessageCount(2)
    assertMockEndpointsSatisfied()
    assertNotNull(task.getLastRun)
  }

  @throws[Exception]
  override protected def createRouteBuilder: RouteBuilder = {
    val alerta = new AlertaImpl(context)
    val builder: CamelTaskRouteBuilder = new CamelTaskRouteBuilder("TestTask", "59 59 23 31 12 ? 2099", Some(500), alerta) {
      override val camelTaskItems = Seq(new ScalaCamelTaskItem[String] {
        var count = 0
        override def bodies(operation: Operation): Stream[String] = operation match {
          case Full() => ???
          case Incremental() =>
            count += 1
            if (count == 1) Stream(MESSAGE_1, MESSAGE_2)
            else if (count == 2) Stream(MESSAGE_3)
            else Stream()
        }
        override def endpoints(body: String): Seq[String] = Seq(MOCK_TRIGGERED)
      })
    }
    task = builder.getTask
    builder
  }
}

object ScalaIncrementalCamelTaskRouteBuilderTest {

  val MESSAGE_1 = "Message 1"
  val MESSAGE_2 = "Message 2"
  val MESSAGE_3 = "Message 3"

}

