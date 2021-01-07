package de.volkerfaas.kafka.deployment.service;

import de.volkerfaas.kafka.deployment.config.TaskConfig;
import de.volkerfaas.kafka.deployment.model.Job;
import de.volkerfaas.kafka.deployment.model.Task;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public interface TaskService {

    Task create(Job job, String key, TaskConfig taskConfig);

    int executeGitTask(Task task);

    int executeCommandLineTask(Task task) throws IOException, InterruptedException;

    Process start(Task task) throws IOException;

}
