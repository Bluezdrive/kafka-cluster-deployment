package de.volkerfaas.kafka.deployment.model;

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

    public Job() {
        super();
        this.tasks = new LinkedHashSet<>();
    }

    public Job(Event event, String repository, String branch) {
        this();
        this.event = event;
        this.repository = repository;
        this.branch = branch;
    }

    @OneToMany(mappedBy = "job", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("orderId ASC")
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
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
