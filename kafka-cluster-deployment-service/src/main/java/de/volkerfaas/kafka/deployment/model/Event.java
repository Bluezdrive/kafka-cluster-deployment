package de.volkerfaas.kafka.deployment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.volkerfaas.kafka.deployment.controller.model.github.PushEvent;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="events")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Event {

    private long id;
    private String ref;
    private String repositoryFullName;
    private String repositoryUrl;
    private Date repositoryPushedAt;
    private String pusherName;
    private String pusherEmail;
    private String senderAvatarUrl;
    private String senderUrl;
    private String headCommitId;
    private String headCommitMessage;
    private Date headCommitTimestamp;
    private String headCommitUrl;

    public Event() {
    }

    public Event(PushEvent event) {
        if (Objects.nonNull(event)) {
            this.ref = event.getRef();
            this.repositoryFullName = event.getRepository().getFullName();
            this.repositoryUrl = event.getRepository().getUrl();
            this.repositoryPushedAt = event.getRepository().getPushedAt();
            this.pusherName = event.getPusher().getName();
            this.pusherEmail = event.getPusher().getEmail();
            this.senderAvatarUrl = event.getSender().getAvatarUrl();
            this.senderUrl = event.getSender().getUrl();
            this.headCommitId = event.getHeadCommit().getId();
            this.headCommitMessage = event.getHeadCommit().getMessage();
            this.headCommitTimestamp = event.getHeadCommit().getTimestamp();
            this.headCommitUrl = event.getHeadCommit().getUrl();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String branch) {
        this.ref = branch;
    }

    public String getRepositoryFullName() {
        return repositoryFullName;
    }

    public void setRepositoryFullName(String repositoryFullName) {
        this.repositoryFullName = repositoryFullName;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public Date getRepositoryPushedAt() {
        return repositoryPushedAt;
    }

    public void setRepositoryPushedAt(Date repositoryPushedAt) {
        this.repositoryPushedAt = repositoryPushedAt;
    }

    public String getPusherName() {
        return pusherName;
    }

    public void setPusherName(String pusherName) {
        this.pusherName = pusherName;
    }

    public String getPusherEmail() {
        return pusherEmail;
    }

    public void setPusherEmail(String pusherEmail) {
        this.pusherEmail = pusherEmail;
    }

    public String getSenderAvatarUrl() {
        return senderAvatarUrl;
    }

    public void setSenderAvatarUrl(String userAvatarUrl) {
        this.senderAvatarUrl = userAvatarUrl;
    }

    public String getSenderUrl() {
        return senderUrl;
    }

    public void setSenderUrl(String senderUrl) {
        this.senderUrl = senderUrl;
    }

    public String getHeadCommitId() {
        return headCommitId;
    }

    public void setHeadCommitId(String headCommitId) {
        this.headCommitId = headCommitId;
    }

    public String getHeadCommitMessage() {
        return headCommitMessage;
    }

    public void setHeadCommitMessage(String headCommitMessage) {
        this.headCommitMessage = headCommitMessage;
    }

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public Date getHeadCommitTimestamp() {
        return headCommitTimestamp;
    }

    public void setHeadCommitTimestamp(Date headCommitTimestamp) {
        this.headCommitTimestamp = headCommitTimestamp;
    }

    public String getHeadCommitUrl() {
        return headCommitUrl;
    }

    public void setHeadCommitUrl(String headCommitUrl) {
        this.headCommitUrl = headCommitUrl;
    }

}
