package io.guanaco.scheduler.camel;

import org.apache.camel.ProducerTemplate;

import java.util.List;

/**
 * Represents a single task item to be performed by {@link JavaCamelTaskRouteBuilder}
 */
public abstract class JavaCamelTaskItem<T> implements CamelTaskItem {

    protected abstract List<T> getExchangeBodies();

    protected abstract List<String> getTargetEndpoints(T body);

    public void handle(ProducerTemplate template) {
        for (T body : getExchangeBodies()) {
            for (String endpoint : getTargetEndpoints(body)) {
                template.sendBody(endpoint, body);
            }
        }
    }

    @Override
    public void handle(ProducerTemplate template, Operation operation) {
        if (operation instanceof Incremental) {
            throw new UnsupportedOperationException("Incremental updates not support by Java CamelTaskItem - use the Scala equivalent instead");
        }
        handle(template);
    }
}
