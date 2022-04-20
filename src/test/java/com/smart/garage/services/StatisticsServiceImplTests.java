package com.smart.garage.services;

import com.smart.garage.repositories.contracts.StatisticsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceImplTests {

    @Mock
    StatisticsRepository statisticsRepository;

    @InjectMocks
    StatisticsServiceImpl statisticsService;

    @Test
    void getNumberOfVehicles_Should_Call_RepoMethod() {
        Mockito.when(statisticsRepository.getNumberOfVehicles()).thenReturn(101L);
        long result = statisticsService.getNumberOfVehicles();
        Mockito.verify(statisticsRepository, Mockito.times(1)).getNumberOfVehicles();
        Assertions.assertEquals(101, result);
    }

    @Test
    void getNumberOfVisits_Should_Call_RepoMethod() {
        Mockito.when(statisticsRepository.getNumberOfVisits()).thenReturn(102L);
        long result = statisticsService.getNumberOfVisits();
        Mockito.verify(statisticsRepository, Mockito.times(1)).getNumberOfVisits();
        Assertions.assertEquals(102, result);
    }

    @Test
    void getNumberOfCustomers_Should_Call_RepoMethod() {
        Mockito.when(statisticsRepository.getNumberOfCustomers()).thenReturn(103L);
        long result = statisticsService.getNumberOfCustomers();
        Mockito.verify(statisticsRepository, Mockito.times(1)).getNumberOfCustomers();
        Assertions.assertEquals(103, result);
    }

    @Test
    void getNumberOfServices_Should_Call_RepoMethod() {
        Mockito.when(statisticsRepository.getNumberOfServices()).thenReturn(104L);
        long result = statisticsService.getNumberOfServices();
        Mockito.verify(statisticsRepository, Mockito.times(1)).getNumberOfServices();
        Assertions.assertEquals(104, result);
    }
}
