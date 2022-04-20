package com.smart.garage.repositories.contracts;

public interface StatisticsRepository {

    long getNumberOfVehicles();

    long getNumberOfVisits();

    long getNumberOfCustomers();

    long getNumberOfServices();

}
