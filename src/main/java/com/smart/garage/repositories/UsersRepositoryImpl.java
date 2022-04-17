package com.smart.garage.repositories;

import com.smart.garage.models.User;
import com.smart.garage.repositories.contracts.UsersRepository;
import com.smart.garage.utility.UsersQueryMaker;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UsersRepositoryImpl extends AbstractCRUDRepository<User> implements UsersRepository {

    public UsersRepositoryImpl() {
        super(User.class);
    }

    @Override
    public List<User> getAllFiltered(int roleId, Optional<String> username, Optional<String> email, Optional<String> phoneNumber,
                                     Optional<String> licenseOrVIN, Optional<String> make, Optional<String> model,
                                     Optional<String> sortBy, Optional<String> sortOrder) {
        UsersQueryMaker queryMaker = new UsersQueryMaker(roleId);
        String query = queryMaker.buildSQLSearchAndSortQuery(username, email, phoneNumber, licenseOrVIN,
                                                            make, model, sortBy, sortOrder);
        HashMap<String, Object> propertiesMap = queryMaker.getPropertiesMap();

        try (Session session = sessionFactory.openSession()) {
            NativeQuery<User> request = session.createSQLQuery(query);
            request.addEntity(User.class);
            request.setProperties(propertiesMap);

            return request.list();
        }
    }

}
