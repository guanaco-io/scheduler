package io.guanaco.scheduler.rest;

import io.guanaco.scheduler.Task;
import io.guanaco.scheduler.TaskStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.format.DateTimeFormatter;

/**
 * JAXB-annotated wrapper for {@link Task}
 */
@XmlRootElement(name = "task")
public class TaskDelegate {

    private final Task task;

    public TaskDelegate() {
        // we need the default constructor for JAXB
        throw new UnsupportedOperationException("Unable to create TaskDelegate without a task");
    }

    public TaskDelegate(Task task) {
        super();
        this.task = task;
    }

    @XmlElement
    public String getSchedule() {
        return task.getSchedule();
    }

    @XmlElement
    public String getIdentifier() {
        return task.getIdentifier();
    }

    @XmlElement
    public String getLastRun() {
        if (task.getLastRun() == null) {
            return "never";
        } else {
            return task.getLastRun().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    @XmlElement
    public TaskStatus getStatus() {
        return task.getStatus();
    }

    protected void trigger() {
        task.trigger();
    }

    protected void trigger(String selector) {
        task.trigger(selector);
    }
}
