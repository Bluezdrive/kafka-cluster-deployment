package de.volkerfaas.kafka.deployment.integration;

import de.volkerfaas.kafka.deployment.controller.model.SkipablePageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    Page<T> findAllBySkipablePageRequest(SkipablePageRequest pageable);

}
