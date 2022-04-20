package com.smart.garage.services;

import com.smart.garage.repositories.contracts.StatisticsRepository;
import com.smart.garage.services.contracts.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;

    @Autowired
    public StatisticsServiceImpl(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public long getNumberOfVehicles() {
        return statisticsRepository.getNumberOfVehicles();
    }

    @Override
    public long getNumberOfVisits() {
        return statisticsRepository.getNumberOfVisits();
    }

    @Override
    public long getNumberOfCustomers() {
        return statisticsRepository.getNumberOfCustomers();
    }

    @Override
    public long getNumberOfServices() {
        return statisticsRepository.getNumberOfServices();
    }
}
