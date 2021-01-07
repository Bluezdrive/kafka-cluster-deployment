package de.volkerfaas.kafka.deployment.service.impl;

import de.volkerfaas.kafka.deployment.config.Config;
import de.volkerfaas.kafka.deployment.config.TaskConfig;
import de.volkerfaas.kafka.deployment.integration.GitRepository;
import de.volkerfaas.kafka.deployment.model.Job;
import de.volkerfaas.kafka.deployment.model.Status;
import de.volkerfaas.kafka.deployment.model.Task;
import de.volkerfaas.kafka.deployment.model.TaskType;
import de.volkerfaas.kafka.deployment.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class TaskServiceImplTest {

    private static final String LIST_FILES_KEY = "list-files";

    private Config config;
    private TaskService taskService;

    @BeforeEach
    void init() {
        final Map<String, String> environment = new LinkedHashMap<>();
        environment.put("VARIABLE", "value");
        final TaskConfig taskConfig = new TaskConfig();
        taskConfig.setOrder(1);
        taskConfig.setName("List files in directory");
        taskConfig.setType(TaskType.COMMAND_LINE);
        taskConfig.setCommand("ls -la");
        taskConfig.setEnvironment(environment);
        this.config = new Config();
        this.config.setWorkingDirectory("/home/workspace/repository");
        this.config.getTasks().put(LIST_FILES_KEY, taskConfig);
        final GitRepository gitRepository = mock(GitRepository.class);
        this.taskService = new TaskServiceImpl(config, gitRepository);
    }

    @Test
    void testCreate() {
        final Job job = new Job();
        final TaskConfig taskConfig = config.getTasks().get(LIST_FILES_KEY);
        final Task task = taskService.create(job, LIST_FILES_KEY, taskConfig);
        assertNotNull(task);
        assertEquals(1, task.getOrderId());
        assertEquals(LIST_FILES_KEY, task.getKey());
        assertEquals("List files in directory", task.getName());
        assertEquals("/home/workspace/repository", task.getDirectory());
        assertEquals("ls -la", task.getCommand());
        assertEquals(TaskType.COMMAND_LINE, task.getType());
        assertEquals(job, task.getJob());
        assertEquals(-1, task.getExitCode());
        assertEquals(Status.CREATED, task.getStatus());
    }

    void testStart() {
        final ProcessBuilder processBuilder = Mockito.mock(ProcessBuilder.class);
    }

}
