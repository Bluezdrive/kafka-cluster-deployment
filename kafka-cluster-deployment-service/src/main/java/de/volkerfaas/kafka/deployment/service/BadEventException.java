package de.volkerfaas.kafka.deployment.service;

public class BadEventException extends Exception {

    private static final String MESSAGE_FORMAT = "Invalid %s %s";

    public BadEventException(String object, String value) {
        super(String.format(MESSAGE_FORMAT, object, value));
    }

}
