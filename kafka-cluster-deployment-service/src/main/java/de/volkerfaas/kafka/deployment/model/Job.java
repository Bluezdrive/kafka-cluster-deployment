package de.volkerfaas.kafka.deployment.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="jobs")
public class Job extends Base {

    private Set<Task> tasks;
    private String repository;
    private String branch;
    private Event event;
    private Set<GitPollingLog> gitPollingLogs;
    private Job reference;

    public Job() {
        super();
        this.tasks = new LinkedHashSet<>();
        this.gitPollingLogs = new LinkedHashSet<>();
    }

    public Job(Event event, String repository, String branch) {
        this();
        this.event = event;
        this.repository = repository;
        this.branch = branch;
    }

    @OneToMany(mappedBy = "job", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("orderId ASC")
    @JsonManagedReference(value = "tasks")
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference(value = "event")
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @OneToMany(mappedBy = "job", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("id DESC")
    @JsonManagedReference(value = "git-polling-logs")
    public Set<GitPollingLog> getGitPollingLogs() {
        return gitPollingLogs;
    }

    public void setGitPollingLogs(Set<GitPollingLog> gitPollingLogs) {
        this.gitPollingLogs = gitPollingLogs;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference(value = "reference")
    @JsonIdentityReference(alwaysAsId = true)
    public Job getReference() {
        return reference;
    }

    public void setReference(Job reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "Job{" +
                super.toString() +
                ", tasks=" + tasks +
                ", repository='" + repository + '\'' +
                ", branch='" + branch + '\'' +
                ", event=" + event +
                '}';
    }

}
