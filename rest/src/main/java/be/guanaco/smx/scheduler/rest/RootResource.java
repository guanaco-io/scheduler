package be.guanaco.smx.scheduler.rest;

import be.guanaco.smx.scheduler.Task;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * JAX-RS resource to interact with scheduled tasks. It allows for:
 * <ul>
 *     <li>listing all existing tasks</li>
 *     <li>trigger a task to run immediately</li>
 * </ul>
 */
@Path("/")
// TODO: this seems overly permissive, doesn't it?
@CrossOriginResourceSharing(allowAllOrigins = true)
public class RootResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootResource.class);

    private final List<TaskDelegate> tasks = new LinkedList<>();

    @GET
    @Produces({"application/json", "text/xml"})
    public List<TaskDelegate> getTasks() {
        return tasks;
    }

    @POST
    @Path("{id}")
    public Response trigger(@PathParam("id") String id, String reason) {
        Optional<TaskDelegate> toTrigger = getTask(id);
        if (toTrigger.isPresent()) {
            LOGGER.info(String.format("Scheduled tasks %s triggered by REST service - reason %s", id, reason));
            toTrigger.get().trigger();
            return Response.accepted().build();
        } else {
            LOGGER.warn(String.format("Unable to trigger scheduled task %s - task does not exist", id));
            return Response.status(Response.Status.NOT_FOUND).entity(String.format("Task %s does not exist", id)).build();
        }
    }

    @POST
    @Path("{id}/subset")
    public Response triggerSubset(@PathParam("id") String id, String selector) {
        Optional<TaskDelegate> toTrigger = getTask(id);
        if (toTrigger.isPresent()) {
            LOGGER.info(String.format("Scheduled tasks %s triggered by REST service - subset %s", id, selector));
            toTrigger.get().trigger(selector);
            return Response.accepted().build();
        } else {
            LOGGER.warn(String.format("Unable to trigger scheduled task %s - task does not exist", id));
            return Response.status(Response.Status.NOT_FOUND).entity(String.format("Task %s does not exist", id)).build();
        }
    }

    public void addTask(Task task) {
        if (task != null) {
            tasks.add(new TaskDelegate(task));
        }
    }

    public void removeTask(Task task) {
        if (task == null) {
            tasks.clear();
        } else {
            Optional<TaskDelegate> toRemove = getTask(task.getIdentifier());
            toRemove.ifPresent((delegate) -> tasks.remove(delegate));
        }
    }

    /* get a task by identifier */
    private Optional<TaskDelegate> getTask(String identifier) {
        return tasks.stream().filter((delegate) -> delegate.getIdentifier().equals(identifier)).findFirst();
    }
}
