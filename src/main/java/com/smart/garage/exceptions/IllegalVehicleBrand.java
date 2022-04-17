package com.smart.garage.exceptions;

public class IllegalVehicleBrand extends RuntimeException{

    public IllegalVehicleBrand() {
    }

    public IllegalVehicleBrand(String message) {
        super(message);
    }
}
