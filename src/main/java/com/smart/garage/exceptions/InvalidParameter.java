package com.smart.garage.exceptions;

public class InvalidParameter extends RuntimeException{

    public InvalidParameter() {
    }

    public InvalidParameter(String message) {
        super(message);
    }
}
