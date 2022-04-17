package com.smart.garage.controllers.rest;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.Servicez;
import com.smart.garage.models.User;
import com.smart.garage.models.dtos.ServicesDTOIn;
import com.smart.garage.services.contracts.ServicesService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.ServicesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
public class ServicesController {

    private final AuthenticationHelper authenticationHelper;
    private final ServicesService servicesService;
    private final ServicesMapper mapper;

    @Autowired
    public ServicesController(AuthenticationHelper authenticationHelper, ServicesService servicesService, ServicesMapper mapper) {
        this.authenticationHelper = authenticationHelper;
        this.servicesService = servicesService;
        this.mapper = mapper;
    }

    @GetMapping()
    public List<Servicez> getAll(@RequestParam(required = false) Optional<String> name,
                                 @RequestParam(required = false) Optional<Integer> priceMinimum,
                                 @RequestParam(required = false) Optional<Integer> priceMaximum,
                                 @RequestParam(required = false) Optional<String> sortBy,
                                 @RequestParam(required = false) Optional<String> sortOrder) {
        try {
            return servicesService.getAll(name, priceMinimum, priceMaximum, sortBy, sortOrder);
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Servicez getAll(@PathVariable int id) {
                try {
            return servicesService.getById(id);
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/new")
    public Servicez create(@Valid @RequestBody ServicesDTOIn dtoIn) {
        User requester = authenticationHelper.getCurrentUser();

        Servicez servicez = mapper.toObject(dtoIn);

        try {
            return servicesService.create(requester, servicez);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}/update")
    public Servicez update(@PathVariable int id, @Valid @RequestBody ServicesDTOIn dtoIn) {
        User requester = authenticationHelper.getCurrentUser();


        try {
            Servicez servicez = mapper.toObject(id, dtoIn);
            return servicesService.update(requester, servicez);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        User requester = authenticationHelper.getCurrentUser();

        try {
            servicesService.delete(requester, id);
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
