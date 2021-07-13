package de.volkerfaas.kafka.deployment.integration.impl;

import de.volkerfaas.kafka.deployment.integration.GitRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class GitRepositoryImpl implements GitRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryImpl.class);
    private static final String BRANCH_PREFIX = "refs/heads/";

    private final TransportConfigCallback transportConfigCallback;

    public GitRepositoryImpl(TransportConfigCallback transportConfigCallback) {
        this.transportConfigCallback = transportConfigCallback;
    }

    @Override
    public Optional<Git> openRepository(String branch, File directory) throws IOException {
        if (!RepositoryCache.FileKey.isGitRepository(new File(directory, ".git"), FS.DETECTED)) {
            return Optional.empty();
        }
        final Git git = Git.open(directory);
        final Repository repository = git.getRepository();
        final Ref refLocal = repository.getRefDatabase().findRef(BRANCH_PREFIX + branch);
        final ObjectId objectId = refLocal.getObjectId();
        if (Objects.isNull(objectId)) {
            return Optional.empty();
        }

        return Optional.of(git);
    }

    @Override
    public Git cloneRepository(String uri, String branch, File directory, ProgressMonitor progressMonitor) throws GitAPIException {
        return Git.cloneRepository()
                .setURI(uri)
                .setDirectory(directory)
                .setBranchesToClone(Collections.singletonList(BRANCH_PREFIX + branch))
                .setProgressMonitor(progressMonitor)
                .setBranch(BRANCH_PREFIX + branch)
                .setTransportConfigCallback(transportConfigCallback)
                .call();
    }

    @Override
    public void addFile(Git git, String file) throws GitAPIException {
        git.add()
                .addFilepattern(file)
                .call();
    }

    @Override
    public String commitRepository(Git git, String message) throws GitAPIException, UnsupportedEncodingException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (final PrintStream hookStdOut = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name())) {
            git.commit()
                    .setMessage(message)
                    .setHookOutputStream(hookStdOut)
                    .setHookErrorStream(hookStdOut)
                    .call();
        }

        return outputStream.toString(StandardCharsets.UTF_8.name());
    }

    @Override
    public void pullRepository(Git git, ProgressMonitor progressMonitor) throws GitAPIException {
        git.pull()
                .setProgressMonitor(progressMonitor)
                .setTransportConfigCallback(transportConfigCallback)
                .call();
    }

    @Override
    public void pushRepository(Git git, ProgressMonitor progressMonitor) throws GitAPIException {
        git.push()
                .setProgressMonitor(progressMonitor)
                .setTransportConfigCallback(transportConfigCallback)
                .call();
    }

    @Override
    public Optional<String> getLocalObjectId(Git git, String branch) throws IOException {
        final Repository repository = git.getRepository();
        final Ref ref = repository.getRefDatabase().findRef(BRANCH_PREFIX + branch);
        if (Objects.isNull(ref)) {
            return Optional.empty();
        }
        final ObjectId objectId = ref.getObjectId();
        if (Objects.isNull(objectId)) {
            return Optional.empty();
        }
        return Optional.of(objectId.name());
    }

    @Override
    public Optional<String> getRemoteObjectId(String uri, String branch) throws GitAPIException {
        final Map<String, Ref> stringRefMap = Git.lsRemoteRepository()
                .setRemote(uri)
                .setHeads(true)
                .setTransportConfigCallback(transportConfigCallback)
                .callAsMap();
        LOGGER.info("Found {} remote heads on {}", stringRefMap.size(), uri);
        final Ref ref = stringRefMap.get(BRANCH_PREFIX + branch);
        if (Objects.isNull(ref)) {
            return Optional.empty();
        }
        final ObjectId objectId = ref.getObjectId();
        if (Objects.isNull(objectId)) {
            return Optional.empty();
        }

        return Optional.of(objectId.name());
    }

}
