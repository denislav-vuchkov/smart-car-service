package com.smart.garage.exceptions;

public class DuplicateEntityException extends RuntimeException{

    public DuplicateEntityException(String type, String attribute, String value) {
        super(String.format("%s with %s %s already exists. Please choose a different %s.",
                type, attribute, value, attribute));
    }

    public DuplicateEntityException(String message) {
        super(message);
    }
}
