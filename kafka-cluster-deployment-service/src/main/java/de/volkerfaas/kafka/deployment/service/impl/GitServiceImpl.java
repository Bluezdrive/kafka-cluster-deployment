package de.volkerfaas.kafka.deployment.service.impl;

import de.volkerfaas.kafka.deployment.config.Config;
import de.volkerfaas.kafka.deployment.integration.GitRepository;
import de.volkerfaas.kafka.deployment.integration.GitStatusRepository;
import de.volkerfaas.kafka.deployment.integration.impl.TaskProgressMonitor;
import de.volkerfaas.kafka.deployment.model.Event;
import de.volkerfaas.kafka.deployment.model.GitStatus;
import de.volkerfaas.kafka.deployment.model.Job;
import de.volkerfaas.kafka.deployment.model.Task;
import de.volkerfaas.kafka.deployment.service.GitService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class GitServiceImpl implements GitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitServiceImpl.class);
    private static final long DAY_MILLIS = 86400000L;

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
    public Git openOrCloneRepository(Task task, File directory, String branch) throws IOException, GitAPIException {
        Git git = gitRepository.openRepository(branch, directory).orElse(null);
        if (Objects.isNull(git)) {
            final String repository = config.getGit().getRepository();
            final String uri = "git@github.com:" + repository + ".git";
            final String command = "git clone " + uri + " .";
            task.setCommand(command);
            git = gitRepository.cloneRepository(uri, branch, directory, new TaskProgressMonitor(task));
        } else {
            final String command = "git pull";
            task.setCommand(command);
            gitRepository.pullRepository(git, new TaskProgressMonitor(task));
        }
        return git;
    }

    @Override
    public boolean isRepositoryChanged(String repository, String branch, Job job) throws GitAPIException {
        if (Objects.isNull(job)) {
            return true;
        }
        final Event event = job.getEvent();
        final String headCommitId = event.getHeadCommitId();
        LOGGER.info("[poll] Last Built Revision: Revision {} (refs/remotes/origin/{})", headCommitId, branch);
        final String uri = "git@github.com:" + repository + ".git";
        final String remoteObjectId = gitRepository.getRemoteObjectId(uri, branch).orElse(null);
        if (Objects.isNull(remoteObjectId)) {
            return false;
        }

        final boolean changed = !Objects.equals(headCommitId, remoteObjectId);
        final GitStatus gitStatus = new GitStatus(repository, branch, headCommitId, remoteObjectId, changed);
        if (!changed) {
            switch (job.getStatus()) {
                case SUCCESS -> LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - already built by {}", branch, remoteObjectId, job.getId());
                case FAILED -> LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - already built with failure by {}", branch, remoteObjectId, job.getId());
                case RUNNING -> LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - is building by {}", branch, remoteObjectId, job.getId());
                case CREATED -> LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - to be build by {}", branch, remoteObjectId, job.getId());
            }
            LOGGER.info("No changes");

            gitStatus.setJob(job);
        } else {
            LOGGER.info("[poll] Latest remote head revision on refs/heads/{} is: {} - not yet build", branch, remoteObjectId);
        }
        gitStatusRepository.saveAndFlush(gitStatus);

        return changed;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void removeOlderThan() {
        final long millis = System.currentTimeMillis() - DAY_MILLIS;
        final int count = gitStatusRepository.removeOlderThan(millis);
        LOGGER.info("Removed {} git status messages older than {}", count, millis);
    }

}
