package be.guanaco.smx.scheduler;

import java.time.LocalDateTime;

/**
 * Represents a scheduled task.
 */
public interface Task {

    /**
     * Get the task schedule in Quartz' cron formt
     *
     * @return the schedule
     */
    String getSchedule();

    /**
     * Get the unique identifier for the task
     *
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Get the last date/time when the task was run.
     *
     * @return the date/time or <code>null</code> if the task has not yet been run
     */
    LocalDateTime getLastRun();

    /**
     * Trigger the task to run immediately.
     */
    void trigger();

    /**
     * Trigger the task to run a subset of the items immediately.
     */
    void trigger(String selector);

    /**
     * Get the task status
     *
     * @return the task status
     */
    TaskStatus getStatus();

}
