package de.volkerfaas.kafka.deployment.integration;

import de.volkerfaas.kafka.deployment.model.Job;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends BaseRepository<Job, Long> {

    @Query("SELECT j.id FROM Job j WHERE j.status = de.volkerfaas.kafka.deployment.model.Status.CREATED OR j.status = de.volkerfaas.kafka.deployment.model.Status.RUNNING ORDER BY j.id ASC")
    List<Long> findNotFinished();

    @Query("SELECT j FROM Job j WHERE j.id = (SELECT max(j.id) FROM Job j)")
    Optional<Job> findLatest();

}
