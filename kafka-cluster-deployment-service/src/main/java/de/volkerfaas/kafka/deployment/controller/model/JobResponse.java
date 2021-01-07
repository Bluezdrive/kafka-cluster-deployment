package de.volkerfaas.kafka.deployment.controller.model;

public class JobResponse {

    private long jobId;

    public JobResponse() {
    }

    public JobResponse(long jobId) {
        this.jobId = jobId;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

}
