package com.smart.garage.services;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.repositories.contracts.UsersRepository;
import com.smart.garage.services.contracts.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsServiceImpl implements MyUserDetailsService {

    public static final String USER_NOT_FOUND_MESSAGE = "User with username %s does not exist";

    private final UsersRepository repository;

    @Autowired
    public MyUserDetailsServiceImpl(UsersRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return repository.getByField("username", username);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username));
        }
    }

}
