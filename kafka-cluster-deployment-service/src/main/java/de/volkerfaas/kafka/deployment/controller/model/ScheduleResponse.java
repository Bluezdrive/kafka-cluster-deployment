package de.volkerfaas.kafka.deployment.controller.model;

public class ScheduleResponse {

    private String schedule;

    public ScheduleResponse() {
    }

    public ScheduleResponse(String schedule) {
        this.schedule = schedule;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

}
