package de.volkerfaas.kafka.deployment.config;

public class GitConfig {

    private String branch;
    private String secret;
    private String repository;
    private String privateKey;
    private long pollRate;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public long getPollRate() {
        return pollRate;
    }

    public void setPollRate(long pollRate) {
        this.pollRate = pollRate;
    }
}
