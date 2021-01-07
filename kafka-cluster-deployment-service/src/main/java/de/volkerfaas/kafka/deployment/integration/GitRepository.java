package de.volkerfaas.kafka.deployment.integration;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface GitRepository {
    Optional<Git> openRepository(String branch, File directory) throws IOException;

    Git cloneRepository(String uri, String branch, File directory, ProgressMonitor progressMonitor) throws GitAPIException;

    void pullRepository(Git git, ProgressMonitor progressMonitor) throws GitAPIException;

    Optional<String> getLocalObjectId(Git git, String branch) throws IOException;

    Optional<String> getRemoteObjectId(String uri, String branch) throws GitAPIException;
}
