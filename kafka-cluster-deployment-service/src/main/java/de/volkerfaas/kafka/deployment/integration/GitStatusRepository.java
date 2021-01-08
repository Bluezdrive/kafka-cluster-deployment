package de.volkerfaas.kafka.deployment.integration;

import de.volkerfaas.kafka.deployment.model.GitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GitStatusRepository  extends JpaRepository<GitStatus, Long> {

    @Query("SELECT gs FROM GitStatus gs LEFT JOIN FETCH gs.job WHERE gs.id = (SELECT max(gs.id) FROM GitStatus gs)")
    //@Query("SELECT gs FROM GitStatus gs WHERE gs.id = (SELECT max(gs.id) FROM GitStatus gs)")
    Optional<GitStatus> findLatest();

    @Query("SELECT gs FROM GitStatus gs WHERE gs.id = (SELECT max(gs.id) FROM GitStatus gs) AND gs.status = de.volkerfaas.kafka.deployment.model.Status.SUCCESS AND gs.changed = false AND gs.repository = :repository AND gs.branch = :branch")
    Optional<GitStatus> findLatestSuccessfulNotChangedByRepositoryAndBranch(@Param("repository") String repository, @Param("branch") String branch);

}
