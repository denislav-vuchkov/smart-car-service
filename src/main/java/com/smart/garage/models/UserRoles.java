package com.smart.garage.models;

public enum UserRoles {
    CUSTOMER(3),
    EMPLOYEE(2);

    final int value;

    UserRoles(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        switch(this) {
            case CUSTOMER: return "Customer";
            case EMPLOYEE: return "Employee";
            default: throw new IllegalArgumentException();
        }
    }

    public int getValue() {
        return value;
    }
}
