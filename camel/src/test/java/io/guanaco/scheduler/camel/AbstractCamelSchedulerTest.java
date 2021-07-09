package io.guanaco.scheduler.camel;

import io.guanaco.alerta.api.Alerta$;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;

/**
 * Abstract base class for unit tests.
 */
public abstract class AbstractCamelSchedulerTest extends CamelTestSupport {

    protected static final String MOCK_TRIGGERED = "mock:trigger";
    protected static final String MOCK_HEARTBEATS = "mock:heartbeats";
    protected static final String MOCK_ALERTS = "mock:alerts";

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        context.addComponent("activemq", ActiveMQComponent.activeMQComponent("vm://test?broker.persistent=false&broker.useJmx=false"));
        return context;
    }

    @Override
    protected RoutesBuilder[] createRouteBuilders() throws Exception {
        return new RoutesBuilder[] {
                createAlertaMockRouteBuilder(),
                createRouteBuilder()
        };
    }

    private RouteBuilder createAlertaMockRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq://" + Alerta$.MODULE$.HEARTBEAT_QUEUE_NAME()).to(MOCK_HEARTBEATS);
                from("activemq://" + Alerta$.MODULE$.ALERT_QUEUE_NAME()).to(MOCK_ALERTS);
            }
        };
    }

}
