package de.volkerfaas.kafka.deployment.integration.impl;

import de.volkerfaas.kafka.deployment.model.Task;
import org.eclipse.jgit.lib.BatchingProgressMonitor;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskProgressMonitor implements ProgressMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskProgressMonitor.class);

    private final Task task;

    public TaskProgressMonitor(Task task) {
        this.task = task;
    }

    @Override
    public void start(int totalTasks) {

    }

    @Override
    public void beginTask(String title, int totalWork) {
        LOGGER.info("Git - {}: {}", title, totalWork);
        task.setLog(task.getLog().concat(title + ": " + totalWork).concat(System.lineSeparator()));
    }

    @Override
    public void update(int completed) {

    }

    @Override
    public void endTask() {

    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
