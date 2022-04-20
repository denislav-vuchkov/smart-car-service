package com.smart.garage.models;

public enum StatusCode {

    REQUESTED(new VisitStatus(1, "Requested")),
    DECLINED(new VisitStatus(2, "Declined")),
    NOT_STARTED(new VisitStatus(3, "Not Started")),
    IN_PROGRESS(new VisitStatus(4, "In Progress")),
    READY_FOR_PICKUP(new VisitStatus(5, "Ready for Pickup")),
    COMPLETED(new VisitStatus(6, "Completed"));

    private final VisitStatus status;

    StatusCode(VisitStatus status) {
        this.status = status;
    }

    public VisitStatus getStatus() {
        return status;
    }
}
