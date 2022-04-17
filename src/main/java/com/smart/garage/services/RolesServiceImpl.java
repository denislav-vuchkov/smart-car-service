package com.smart.garage.services;

import com.smart.garage.models.Role;
import com.smart.garage.repositories.contracts.RolesRepository;
import com.smart.garage.services.contracts.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesServiceImpl implements RolesService {

    private final RolesRepository repository;

    @Autowired
    public RolesServiceImpl(RolesRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Role> getAll() {
        return repository.getAll();
    }

}
