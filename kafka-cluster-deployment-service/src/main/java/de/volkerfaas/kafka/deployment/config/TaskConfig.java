package de.volkerfaas.kafka.deployment.config;

import de.volkerfaas.kafka.deployment.model.TaskType;

import java.util.HashMap;
import java.util.Map;

public class TaskConfig {

    private String name;
    private int order;
    private TaskType type;
    private String command;
    private Map<String, String> environment;

    public TaskConfig() {
        this.environment = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

}
