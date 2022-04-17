package com.smart.garage.repositories;

import com.smart.garage.models.VehicleMake;
import com.smart.garage.repositories.contracts.VehicleMakeRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VehicleMakeRepositoryImpl extends AbstractCRUDRepository<VehicleMake> implements VehicleMakeRepository {

    public VehicleMakeRepositoryImpl() {
        super(VehicleMake.class);
    }

    @Override
    public List<VehicleMake> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<VehicleMake> request = session.createNativeQuery(
                    "SELECT * FROM vehicle_makes ORDER BY name ", VehicleMake.class);
            return request.list();
        }
    }
}
