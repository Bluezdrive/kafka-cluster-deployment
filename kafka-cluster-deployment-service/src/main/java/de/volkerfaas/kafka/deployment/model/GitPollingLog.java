package de.volkerfaas.kafka.deployment.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;

import javax.persistence.*;

@Entity
@Table(name="git_polling_log")
public class GitPollingLog extends Base {

    private String repository;
    private String branch;
    private String headCommitId;
    private String remoteObjectId;
    private boolean changed;
    private String error;
    private Job job;

    public GitPollingLog() {
    }

    public GitPollingLog(String repository, String branch) {
        this.setStartTimeMillis(System.currentTimeMillis());
        this.repository = repository;
        this.branch = branch;
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

    public String getHeadCommitId() {
        return headCommitId;
    }

    public void setHeadCommitId(String headCommitId) {
        this.headCommitId = headCommitId;
    }

    public String getRemoteObjectId() {
        return remoteObjectId;
    }

    public void setRemoteObjectId(String remoteObjectId) {
        this.remoteObjectId = remoteObjectId;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_id")
    @JsonIdentityReference(alwaysAsId = true)
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

}
