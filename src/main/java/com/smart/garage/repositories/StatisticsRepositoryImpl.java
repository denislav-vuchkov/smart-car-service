package com.smart.garage.repositories;

import com.smart.garage.repositories.contracts.StatisticsRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final SessionFactory sessionFactory;

    public StatisticsRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public long getNumberOfVehicles() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(id) from Vehicle where isDeleted = false ", Long.class);
            return query.getSingleResult();
        }
    }

    @Override
    public long getNumberOfVisits() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(id) from Visit where isDeleted = false ", Long.class);
            return query.getSingleResult();
        }
    }

    @Override
    public long getNumberOfCustomers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(id) from User where isDeleted = false and role.id = 3", Long.class);
            return query.getSingleResult();
        }
    }

    @Override
    public long getNumberOfServices() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(id) from Servicez ", Long.class);
            return query.getSingleResult();
        }
    }
}
