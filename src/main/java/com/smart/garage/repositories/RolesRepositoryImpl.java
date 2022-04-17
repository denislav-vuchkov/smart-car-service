package com.smart.garage.repositories;

import com.smart.garage.models.Role;
import com.smart.garage.repositories.contracts.RolesRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RolesRepositoryImpl extends AbstractCRUDRepository<Role> implements RolesRepository {

    public RolesRepositoryImpl() {
        super(Role.class);
    }
}
