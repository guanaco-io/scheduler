package be.guanaco.smx.scheduler.camel;

import be.guanaco.smx.alerta.api.Alerta;
import be.guanaco.smx.alerta.impl.AlertaImpl;
import be.guanaco.smx.scheduler.Task;
import be.guanaco.smx.scheduler.TaskStatus;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Test cases for building {@link Task}s with Camel routes - exception during scheduled task.
 */
public class CamelTaskRouteBuilderExceptionTest extends AbstractCamelSchedulerTest {

    private Task task;

    @Test
    public void testTriggerFails() throws InterruptedException {
        // 2 exchange: 1 sent to the seda: endpoint, 1 to handle the actual trigger in the route
        NotifyBuilder builder = new NotifyBuilder(context).whenDone(2).create();

        getMockEndpoint(MOCK_TRIGGERED).expectedMessageCount(0);
        getMockEndpoint(MOCK_HEARTBEATS).expectedMessageCount(0);

        assertTrue(builder.matches(10, TimeUnit.SECONDS));
        assertMockEndpointsSatisfied();

        assertNull(task.getLastRun());
        //TODO: there still appears to be a race condition here
        assertEquals(TaskStatus.IDLE, task.getStatus());
    }

    protected RouteBuilder createRouteBuilder() {
        Alerta alerta = new AlertaImpl(context);
        JavaCamelTaskRouteBuilder builder = new JavaCamelTaskRouteBuilder("TestTask", "*/5 * * * * ? *", alerta) {
            @Override
            protected List<CamelTaskItem> getCamelTaskItems() {
                return Collections.singletonList(new JavaCamelTaskItem<String>() {
                    @Override
                    protected List<String> getExchangeBodies() {
                        throw new RuntimeException("Unable to create exchanges at this time!");
                    }

                    @Override
                    protected List<String> getTargetEndpoints(String body) {
                        return Collections.singletonList(MOCK_TRIGGERED);
                    }
                });
            }
        };
        task = builder.getTask();
        return builder;
    }
}
