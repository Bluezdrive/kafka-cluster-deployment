package de.volkerfaas.kafka.deployment.integration;

import de.volkerfaas.kafka.deployment.model.GitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;

public interface GitStatusRepository  extends JpaRepository<GitStatus, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM GitStatus gs WHERE gs.createdDate < :date")
    int removeOlderThan(@Param("date") long millis);

}
