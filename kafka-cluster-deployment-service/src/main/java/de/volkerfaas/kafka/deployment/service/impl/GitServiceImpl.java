package de.volkerfaas.kafka.deployment.service.impl;

import de.volkerfaas.kafka.deployment.config.Config;
import de.volkerfaas.kafka.deployment.integration.GitRepository;
import de.volkerfaas.kafka.deployment.integration.GitStatusRepository;
import de.volkerfaas.kafka.deployment.integration.impl.TaskProgressMonitor;
import de.volkerfaas.kafka.deployment.model.*;
import de.volkerfaas.kafka.deployment.service.GitService;
import de.volkerfaas.kafka.deployment.service.NotFoundException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class GitServiceImpl implements GitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitServiceImpl.class);

    private final Config config;
    private final GitRepository gitRepository;
    private final GitStatusRepository gitStatusRepository;

    @Autowired
    public GitServiceImpl(Config config, GitRepository gitRepository, GitStatusRepository gitStatusRepository) {
        this.config = config;
        this.gitRepository = gitRepository;
        this.gitStatusRepository = gitStatusRepository;
    }

    @Override
    public Git cloneOrPullRepository(Task task, File directory, String branch) throws IOException, GitAPIException {
        Git git = gitRepository.openRepository(branch, directory).orElse(null);
        if (Objects.isNull(git)) {
            final String repository = config.getGit().getRepository();
            final String uri = "git@github.com:" + repository + ".git";
            task.setCommand("git clone " + uri + " .");
            task.addLog("Clone repository " + uri + " into " + directory + "...");
            git = gitRepository.cloneRepository(uri, branch, directory, new TaskProgressMonitor(task));
        } else {
            task.setCommand("git pull");
            task.addLog("Pull repository " + directory + "...");
            gitRepository.pullRepository(git, new TaskProgressMonitor(task));
        }
        return git;
    }

    @Override
    public GitPollingLog findLatest() throws NotFoundException {
        return gitStatusRepository.findLatest().orElseThrow(() -> new NotFoundException(GitPollingLog.class, 0L));
    }

    @Override
    public boolean isRepositoryChanged(String repository, String branch, Job job) {
        final GitPollingLog gitPollingLog = new GitPollingLog(repository, branch);
        if (Objects.isNull(job)) {
            LOGGER.info("[poll] Last Built Revision: No job available");
            createGitStatusEntry(gitPollingLog, true, Status.FAILED, "No job available");

            return true;
        }
        gitPollingLog.setJob(job);

        final Event event = job.getEvent();
        if (Objects.isNull(event)) {
            LOGGER.info("[poll] Last Built Revision: No event for job {} available", job.getId());
            createGitStatusEntry(gitPollingLog, true, Status.FAILED, "No event for job " + job.getId() + " available");

            return true;
        }

        final String headCommitId = event.getHeadCommitId();
        if (Objects.isNull(headCommitId)) {
            LOGGER.info("[poll] Last Built Revision: No Revision (refs/remotes/origin/{})", branch);
        } else {
            LOGGER.info("[poll] Last Built Revision: Revision {} (refs/remotes/origin/{})", headCommitId, branch);
        }
        gitPollingLog.setHeadCommitId(headCommitId);

        final String uri = "git@github.com:" + repository + ".git";
        try {
            final String remoteObjectId = gitRepository.getRemoteObjectId(uri, branch).orElse(null);
            if (Objects.isNull(remoteObjectId)) {
                LOGGER.error("[poll] No remote head revision on refs/heads/{}", branch);
                createGitStatusEntry(gitPollingLog, false, Status.FAILED, "No remote object ID found");

                return false;
            }
            gitPollingLog.setRemoteObjectId(remoteObjectId);

            if (Objects.equals(headCommitId, remoteObjectId)) {
                switch (job.getStatus()) {
                    case SUCCESS -> LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - already built by {}", branch, remoteObjectId, job.getId());
                    case FAILED -> LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - already built with failure by {}", branch, remoteObjectId, job.getId());
                    case RUNNING -> LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - is building by {}", branch, remoteObjectId, job.getId());
                    case CREATED -> LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - to be build by {}", branch, remoteObjectId, job.getId());
                }
                LOGGER.info("No changes");
                updateGitStatusEntry(gitPollingLog, false, Status.SUCCESS);

                return false;
            }

            LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - not yet build", branch, remoteObjectId);
            createGitStatusEntry(gitPollingLog, true, Status.SUCCESS, null);

            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            createGitStatusEntry(gitPollingLog, false, Status.FAILED, e.getMessage());

            return false;
        }
    }

    private void createGitStatusEntry(GitPollingLog gitPollingLog, boolean changed, Status status, String error) {
        gitPollingLog.setChanged(changed);
        gitPollingLog.setStatus(status);
        gitPollingLog.setEndTimeMillis(System.currentTimeMillis());
        gitPollingLog.setError(error);
        gitStatusRepository.saveAndFlush(gitPollingLog);
    }

    private void updateGitStatusEntry(GitPollingLog gitPollingLog, boolean changed, Status status) {
        final String repository = gitPollingLog.getRepository();
        final String branch = gitPollingLog.getBranch();
        final GitPollingLog latestGitPollingLog = gitStatusRepository.findLatestSuccessfulNotChangedByRepositoryAndBranch(repository, branch).orElse(new GitPollingLog());
        latestGitPollingLog.setRepository(repository);
        latestGitPollingLog.setBranch(branch);
        latestGitPollingLog.setHeadCommitId(gitPollingLog.getHeadCommitId());
        latestGitPollingLog.setRemoteObjectId(gitPollingLog.getRemoteObjectId());
        latestGitPollingLog.setChanged(changed);
        latestGitPollingLog.setStatus(status);
        latestGitPollingLog.setJob(gitPollingLog.getJob());
        latestGitPollingLog.setStartTimeMillis(gitPollingLog.getStartTimeMillis());
        latestGitPollingLog.setEndTimeMillis(System.currentTimeMillis());
        gitStatusRepository.saveAndFlush(latestGitPollingLog);
    }

}
