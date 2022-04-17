package com.smart.garage.repositories;

import com.smart.garage.models.Servicez;
import com.smart.garage.repositories.contracts.ServicesRepository;
import com.smart.garage.utility.ServicesQueryMaker;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class ServicesRepositoryImpl extends AbstractCRUDRepository<Servicez> implements ServicesRepository {

    public ServicesRepositoryImpl() {
        super(Servicez.class);
    }

    @Override
    public List<Servicez> getAll(Optional<String> name, Optional<Integer> priceMinimum, Optional<Integer> priceMaximum,
                                 Optional<String> sortBy, Optional<String> sortOrder) {
        try (Session session = sessionFactory.openSession()) {

            ServicesQueryMaker queryMaker = new ServicesQueryMaker();
            String query = queryMaker.buildHQLSearchAndSortQuery(name, priceMinimum, priceMaximum, sortBy, sortOrder);
            HashMap<String, Object> propertiesMap = queryMaker.getProperties();

            Query<Servicez> request = session.createQuery(query, Servicez.class);
            request.setProperties(propertiesMap);

            return request.list();
        }
    }

    @Override
    public Servicez getMostExpensiveService() {
        try (Session session = sessionFactory.openSession()) {
            Query<Servicez> request = session.createNativeQuery(" select * from services order by price_bgn desc limit 1",
                    Servicez.class);

            return request.getSingleResult();
        }
    }

    @Override
    public Servicez getByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Servicez> query = session.createQuery(" from Servicez where name = :name ", Servicez.class);
            query.setParameter("name", name);

            List<Servicez> result = query.list();

            if (result.isEmpty()) {
                return null;
            } else {
                return result.get(0);
            }
        }
    }
}
