package de.volkerfaas.kafka.deployment.service;

import de.volkerfaas.kafka.deployment.controller.model.github.PushEvent;
import de.volkerfaas.kafka.deployment.controller.model.SkipablePageRequest;
import de.volkerfaas.kafka.deployment.model.Job;
import org.springframework.data.domain.Page;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface JobService {

    long queueNewJob(PushEvent event) throws BadEventException, InterruptedException ;

    Job findJobById(long id) throws NotFoundException;

    Page<Job> listJobs(SkipablePageRequest pageable);

    boolean isValidGitHubSignature(String signature, String payload) throws NoSuchAlgorithmException, BadEventException, InvalidKeyException;

}
