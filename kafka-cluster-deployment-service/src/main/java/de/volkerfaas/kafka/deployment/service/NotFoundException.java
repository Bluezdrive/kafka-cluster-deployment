package de.volkerfaas.kafka.deployment.service;

public class NotFoundException extends Exception {

    private static final String MESSAGE_FORMAT = "%s not for id %s";

    private final Object id;

    public NotFoundException(Class<?> clazz, Object id) {
        super(String.format(MESSAGE_FORMAT, clazz.getSimpleName(), id));
        this.id = id;
    }

    public NotFoundException(String message) {
        super(message);
        this.id = 0L;
    }

    public Object getId() {
        return id;
    }

}
