package de.volkerfaas.kafka.deployment.service.impl;

import de.volkerfaas.kafka.deployment.config.Config;
import de.volkerfaas.kafka.deployment.config.TaskConfig;
import de.volkerfaas.kafka.deployment.model.*;
import de.volkerfaas.kafka.deployment.service.GitService;
import de.volkerfaas.kafka.deployment.service.TaskService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final Config config;
    private final GitService gitService;

    public TaskServiceImpl(Config config, GitService gitService) {
        this.config = config;
        this.gitService = gitService;
    }

    @Override
    public Task create(Job job, String key, TaskConfig taskConfig) {
        final String directory = config.getWorkingDirectory();
        final TaskType type = taskConfig.getType();
        final int order = taskConfig.getOrder();
        final String name = taskConfig.getName();
        final String command = taskConfig.getCommand();

        return new Task(job, order, key, name, directory, command, type);
    }

    @Override
    public int executeGitTask(Task task) {
        try {
            final File directory = new File(config.getWorkingDirectory());
            final String branch = config.getGit().getBranch();
            final Git git = gitService.cloneOrPullRepository(task, directory, branch);
            final Job job = task.getJob();
            updateJobEvent(branch, git, job);
            git.close();
            task.setStatus(Status.SUCCESS);
            return 0;
        } catch (GitAPIException | IOException e) {
            LOGGER.error(e.getMessage());
            task.addLog(e.getMessage());
            task.setStatus(Status.FAILED);
            return 1;
        }
    }


    @Override
    public int executeCommandLineTask(Task task) throws IOException, InterruptedException {
        final Process process = start(task);
        final BufferedReader standardOutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final String log = standardOutReader.lines()
                .peek(LOGGER::info)
                .collect(Collectors.joining(System.lineSeparator()));
        final int exitCode = process.waitFor();
        task.addLog(log);
        task.setStatus(exitCode == 0 ? Status.SUCCESS : Status.FAILED);
        task.setEndTimeMillis(System.currentTimeMillis());
        task.setExitCode(exitCode);
        process.destroy();

        return exitCode;
    }

    @Override
    public Process start(Task task) throws IOException {
        final String directory = config.getWorkingDirectory();
        final TaskConfig taskConfig = config.getTasks().get(task.getKey());
        final Map<String, String> environment = taskConfig.getEnvironment();
        final String command = task.getCommand();
        final ProcessBuilder builder = new ProcessBuilder(command.split(" "))
                .directory(new File(directory))
                .redirectErrorStream(true);
        builder.environment().putAll(System.getenv());
        builder.environment().putAll(environment);
        LOGGER.debug(command);

        return builder.start();
    }

    private void updateJobEvent(String branch, Git git, Job job) throws GitAPIException {
        final RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
        final Event event = job.getEvent();
        event.setRef("refs/heads/" + branch);
        event.setHeadCommitMessage(latestCommit.getShortMessage());
        event.setHeadCommitId(latestCommit.getId().name());
        event.setPusherName(latestCommit.getCommitterIdent().getName());
        event.setPusherEmail(latestCommit.getCommitterIdent().getEmailAddress());
        event.setHeadCommitTimestamp(new Date(latestCommit.getCommitTime()));
    }

}
