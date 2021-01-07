package de.volkerfaas.kafka.deployment.integration;

import de.volkerfaas.kafka.deployment.model.Job;

public interface JobProducer {
    Job send(Job job);
}
