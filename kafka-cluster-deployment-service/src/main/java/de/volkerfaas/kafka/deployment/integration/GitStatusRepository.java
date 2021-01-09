package de.volkerfaas.kafka.deployment.integration;

import de.volkerfaas.kafka.deployment.model.GitPollingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GitStatusRepository  extends JpaRepository<GitPollingLog, Long> {

    @Query("SELECT gs FROM GitPollingLog gs LEFT JOIN FETCH gs.job WHERE gs.id = (SELECT max(gs.id) FROM GitPollingLog gs)")
    //@Query("SELECT gs FROM GitStatus gs WHERE gs.id = (SELECT max(gs.id) FROM GitStatus gs)")
    Optional<GitPollingLog> findLatest();

    @Query("SELECT gs FROM GitPollingLog gs WHERE gs.id = (SELECT max(gs.id) FROM GitPollingLog gs) AND gs.status = de.volkerfaas.kafka.deployment.model.Status.SUCCESS AND gs.changed = false AND gs.repository = :repository AND gs.branch = :branch")
    Optional<GitPollingLog> findLatestSuccessfulNotChangedByRepositoryAndBranch(@Param("repository") String repository, @Param("branch") String branch);

}
