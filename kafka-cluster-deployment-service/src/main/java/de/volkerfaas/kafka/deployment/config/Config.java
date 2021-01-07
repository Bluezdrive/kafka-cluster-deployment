package de.volkerfaas.kafka.deployment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "config")
public class Config {

    private GitConfig git;
    private String workingDirectory;
    private Map<String, TaskConfig> tasks;

    public Config() {
        this.tasks = new HashMap<>();
    }

    public GitConfig getGit() {
        return git;
    }

    public void setGit(GitConfig git) {
        this.git = git;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public Map<String, TaskConfig> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, TaskConfig> tasks) {
        this.tasks = tasks;
    }
}
