package com.smart.garage.repositories;

import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;
import com.smart.garage.repositories.contracts.VehicleRepository;
import com.smart.garage.utility.VehicleQueryMaker;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class VehicleRepositoryImpl extends AbstractCRUDRepository<Vehicle> implements VehicleRepository {

    public static final String GET_OWNER_IDS = "select * from users where (lower(username) like lower(:owner) or lower(email) like (:owner) or phone_number like (:owner))";
    public static final String GET_MAKE_NAMES = "select name from vehicle_makes where lower(name) like lower(:make)";
    public static final String GET_MODEL_NAMES = "select name from vehicle_models where lower(name) like lower(:model)";

    public VehicleRepositoryImpl() {
        super(Vehicle.class);
    }

    // TODO Cyrillic

    @Override
    public List<Vehicle> getAll(Optional<String> identifier, Optional<Set<Integer>> owner,
                                Optional<Set<String>> brand, Optional<Set<Integer>> year,
                                Optional<String> sorting, Optional<String> order, boolean excludeDeleted) {

        VehicleQueryMaker queryMaker = new VehicleQueryMaker();
        if (excludeDeleted) queryMaker.excludeDeleted();
        queryMaker.setFilterClause(identifier, owner, brand, year);
        queryMaker.setSortClause(sorting, order);

        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Vehicle> results = session.createSQLQuery(queryMaker.getQuery());
            results.addEntity(Vehicle.class);
            results.setProperties(queryMaker.getParams());
            return results.list();
        }
    }

    @Override
    public List<Vehicle> getAll(Optional<String> identifier, Optional<String> owner,
                                Optional<String> make, Optional<String> model, Optional<Set<Integer>> year,
                                Optional<String> sorting, Optional<String> order, boolean excludeDeleted) {

        Set<Integer> ownerIDs = getOwners(owner)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Set<String> brandNames = new HashSet<>();
        brandNames.addAll(getNames("make", make, GET_MAKE_NAMES));
        brandNames.addAll(getNames("model", model, GET_MODEL_NAMES));

        return getAll(identifier,
                Optional.ofNullable(ownerIDs.isEmpty() ? null : ownerIDs),
                Optional.ofNullable(brandNames.isEmpty() ? null : brandNames),
                year, sorting, order, excludeDeleted);
    }

    private Set<User> getOwners(Optional<String> value) {
        Set<User> results = new HashSet<>();
        if (value.isPresent()) {
            try (Session session = sessionFactory.openSession()) {
                NativeQuery<User> owners = session.createNativeQuery(VehicleRepositoryImpl.GET_OWNER_IDS, User.class);
                owners.setParameter("owner", "%" + value.get().trim() + "%");
                results = new HashSet<>(owners.list());
            }
        }
        return results;
    }

    private Set<String> getNames(String key, Optional<String> value, String sqlString) {
        Set<String> results = new HashSet<>();
        if (value.isPresent()) {
            try (Session session = sessionFactory.openSession()) {
                Query<String> names = session.createSQLQuery(sqlString);
                names.setParameter(key, "%" + value.get().trim() + "%");
                results = new HashSet<>(names.list());
            }
        }
        return results;
    }
}
