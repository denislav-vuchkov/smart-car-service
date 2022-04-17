package com.smart.garage.repositories;

import com.smart.garage.models.ServiceRecord;
import com.smart.garage.repositories.contracts.ServiceRecordRepository;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class ServiceRecordRepositoryImpl extends AbstractCRUDRepository<ServiceRecord> implements ServiceRecordRepository {

    public ServiceRecordRepositoryImpl() {
        super(ServiceRecord.class);
    }

    @Override
    public Set<ServiceRecord> getAll(int visitID) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<ServiceRecord> results = session.createNativeQuery(
                    "SELECT * FROM history_of_services WHERE visit_id = :visitID", ServiceRecord.class);
            results.setParameter("visitID", visitID);
            return new HashSet<>(results.list());
        }
    }
}

