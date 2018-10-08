package be.guanaco.smx.scheduler.camel;

import be.guanaco.smx.alerta.api.Alerta;
import be.guanaco.smx.scheduler.Task;
import org.apache.camel.builder.RouteBuilder;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.List;

/**
 * Abstract Camel {@link RouteBuilder} implementation to implement an {@link Task} using Camel routes.
 * It also provides out-of-the-box integration with Alerta (sending heartbeats for every run of the task).
 */
public abstract class JavaCamelTaskRouteBuilder extends CamelTaskRouteBuilder {


    public JavaCamelTaskRouteBuilder(String identifier, String schedule, Alerta alerta) {
        super(identifier, schedule, alerta);
    }

    @Override
    public Seq<CamelTaskItem> camelTaskItems() {
        return JavaConversions.asScalaBuffer(getCamelTaskItems()).toSeq();
    }

    /**
     * Get the task items that need to be handled by this scheduled task.
     */
    protected abstract List<CamelTaskItem> getCamelTaskItems();

}