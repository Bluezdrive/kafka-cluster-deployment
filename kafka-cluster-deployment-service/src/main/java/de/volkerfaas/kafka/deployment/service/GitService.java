package de.volkerfaas.kafka.deployment.service;

import de.volkerfaas.kafka.deployment.model.GitPollingLog;
import de.volkerfaas.kafka.deployment.model.Job;
import de.volkerfaas.kafka.deployment.model.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public interface GitService {

    Git cloneOrPullRepository(Task task, File directory, String branch) throws IOException, GitAPIException;
    Git commitAndPushRepository(Task task, File directory, String branch) throws IOException, GitAPIException;
    GitPollingLog findLatest() throws NotFoundException;
    boolean isRepositoryChanged(String repository, String branch, Job job);

}
