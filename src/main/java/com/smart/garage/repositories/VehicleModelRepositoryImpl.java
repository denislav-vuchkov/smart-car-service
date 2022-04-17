package com.smart.garage.repositories;

import com.smart.garage.models.VehicleModel;
import com.smart.garage.repositories.contracts.VehicleModelRepository;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VehicleModelRepositoryImpl extends AbstractCRUDRepository<VehicleModel> implements VehicleModelRepository {

    public VehicleModelRepositoryImpl() {
        super(VehicleModel.class);
    }

    @Override
    public List<VehicleModel> getAll() {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<VehicleModel> results = session.createSQLQuery("SELECT * FROM vehicle_models " +
                    "LEFT JOIN vehicle_makes vb on vehicle_models.make_id = vb.id " +
                    "ORDER BY vb.name, vehicle_models.name");
            results.addEntity(VehicleModel.class);
            return results.list();
        }
    }
}
