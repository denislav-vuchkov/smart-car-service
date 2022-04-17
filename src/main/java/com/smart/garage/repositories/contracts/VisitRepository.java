package com.smart.garage.repositories.contracts;

import com.smart.garage.models.Visit;
import com.smart.garage.models.VisitStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VisitRepository extends BaseCRUDRepository<Visit> {

    List<Visit> getAll(Optional<Set<Integer>> owner, Optional<Set<Integer>> vehicle, Optional<String> status,
                       Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate,
                       Optional<String> sorting, Optional<String> order, boolean excludeDeleted);

    List<VisitStatus> getStatus();

}
