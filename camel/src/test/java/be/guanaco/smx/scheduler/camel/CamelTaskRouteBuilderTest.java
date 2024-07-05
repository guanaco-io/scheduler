package be.guanaco.smx.scheduler.camel;

import io.guanaco.alerta.api.Alerta;
import io.guanaco.alerta.impl.AlertaImpl;
import be.guanaco.smx.scheduler.Task;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test cases for building {@link Task}s with Camel routes - scheduled task gets processed successfully.
 */
public class CamelTaskRouteBuilderTest extends AbstractCamelSchedulerTest {

    private static final String MESSAGE_1 = "First message";
    private static final String MESSAGE_2 = "Second message";

    private Task task;

    @Test
    public void testTriggerSucceeds() throws InterruptedException {
        getMockEndpoint(MOCK_TRIGGERED).expectedBodiesReceived(MESSAGE_1, MESSAGE_2);
        getMockEndpoint(MOCK_HEARTBEATS).expectedMessageCount(1);
        assertMockEndpointsSatisfied();

        assertNotNull(task.getLastRun());
    }


    protected RouteBuilder createRouteBuilder() {
        Alerta alerta = new AlertaImpl(context);
        JavaCamelTaskRouteBuilder builder = new JavaCamelTaskRouteBuilder("TestTask", "*/5 * * * * ? *", alerta) {

            @Override
            protected List<CamelTaskItem> getCamelTaskItems() {
                return Collections.singletonList(new JavaCamelTaskItem<String>() {
                    @Override
                    protected List<String> getExchangeBodies() {
                        return Arrays.asList(MESSAGE_1, MESSAGE_2);
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
