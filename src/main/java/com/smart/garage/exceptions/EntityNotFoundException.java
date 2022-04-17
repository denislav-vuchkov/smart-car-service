package com.smart.garage.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String type, int id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s does not exist.", type, attribute, value));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String type, String message) {
        super(String.format("No %s with the %s has been found in the system.", type, message));
    }

    public EntityNotFoundException() {
    }
}
