package com.smart.garage.repositories;

import com.smart.garage.models.Visit;
import com.smart.garage.models.VisitStatus;
import com.smart.garage.repositories.contracts.VisitRepository;
import com.smart.garage.utility.VisitQueryMaker;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class VisitRepositoryImpl extends AbstractCRUDRepository<Visit> implements VisitRepository {

    public VisitRepositoryImpl() {
        super(Visit.class);
    }

    @Override
    public List<Visit> getAll(Optional<Set<Integer>> owner, Optional<Set<Integer>> vehicle, Optional<String> status,
                              Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate,
                              Optional<String> sorting, Optional<String> order, boolean excludeDeleted) {

        VisitQueryMaker queryMaker = new VisitQueryMaker();
        if (excludeDeleted) queryMaker.excludeDeleted();
        queryMaker.setFilterClause(owner, vehicle, status, startDate, endDate);
        queryMaker.setSortClause(sorting, order);

        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Visit> results = session.createSQLQuery(queryMaker.getQuery());
            results.addEntity(Visit.class);
            results.setProperties(queryMaker.getParams());
            return results.list();
        }
    }

    @Override
    public List<VisitStatus> getStatus() {
        try (Session session = sessionFactory.openSession()) {
            Query<VisitStatus> request = session.createNativeQuery("select * from visit_status order by id ", VisitStatus.class);
            return request.list();
        }
    }
}
