package de.volkerfaas.kafka.deployment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="git_status")
public class GitStatus {

    private long id;
    private long createdDate;
    private long lastModifiedDate;
    private String repository;
    private String branch;
    private String headCommitId;
    private String remoteObjectId;
    private boolean changed;
    private Job job;

    public GitStatus() {
    }

    public GitStatus(String repository, String branch, String headCommitId, String remoteObjectId, boolean changed) {
        this.repository = repository;
        this.branch = branch;
        this.headCommitId = headCommitId;
        this.remoteObjectId = remoteObjectId;
        this.changed = changed;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @CreatedDate
    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    @LastModifiedDate
    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="job_id", nullable = false)
    @JsonIgnore
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

}
