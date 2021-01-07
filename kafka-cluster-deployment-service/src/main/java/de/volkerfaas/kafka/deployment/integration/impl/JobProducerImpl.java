package de.volkerfaas.kafka.deployment.integration.impl;

import de.volkerfaas.kafka.deployment.integration.JobProducer;
import de.volkerfaas.kafka.deployment.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobProducerImpl implements JobProducer {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public JobProducerImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public Job send(Job job) {
        simpMessagingTemplate.convertAndSend("/topic/job", job);

        return job;
    }

}
