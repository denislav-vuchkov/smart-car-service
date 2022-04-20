package com.smart.garage.services.contracts;

import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;
import com.smart.garage.models.Visit;
import com.smart.garage.models.VisitStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VisitService {

    List<Visit> getAll(User requester,
                       Optional<Set<Integer>> owner, Optional<Set<Integer>> vehicle, Optional<String> status,
                       Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate,
                       Optional<String> sorting, Optional<String> order, boolean excludeDeleted);

    List<Visit> getAll(User requester, boolean excludeDeleted);

    Visit getById(User requester, int id);

    Visit create(User requester, Visit visit, Set<Integer> serviceIDs);

    Visit create(User requester, User user, Vehicle vehicle, Visit visit, Set<Integer> serviceIDs);

    void accept(User requester, int id);

    Visit update(User requester, Visit visit, Set<Integer> serviceIDs);

    void softDelete(User requester, int id);

    List<VisitStatus> getStatus();

}
