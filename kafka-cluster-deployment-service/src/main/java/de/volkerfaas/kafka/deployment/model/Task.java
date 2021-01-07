package de.volkerfaas.kafka.deployment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name="tasks")
public class Task extends Base {

    private int orderId;
    private String key;
    private String name;
    private String directory;
    private String command;
    private TaskType type;
    private Job job;
    private int exitCode;
    private String log;

    public Task() {
        super();
        this.exitCode = -1;
        this.log = "";
    }

    public Task(Job job, int orderId, String key, String name, String directory, String command, TaskType type) {
        this();
        this.job = job;
        this.orderId = orderId;
        this.key = key;
        this.name = name;
        this.directory = directory;
        this.command = command;
        this.type = type;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int order) {
        this.orderId = order;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String name) {
        this.key = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_id", nullable = false)
    @JsonIgnore
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    @Column(columnDefinition = "TEXT")
    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public String toString() {
        return "Task{" +
                super.toString() +
                ", orderId=" + orderId +
                ", name='" + key + '\'' +
                ", directory='" + directory + '\'' +
                ", command='" + command + '\'' +
                ", type=" + type +
                ", exitCode=" + exitCode +
                ", log='" + log + '\'' +
                '}';
    }

}
