package com.smart.garage.controllers.rest;

import com.smart.garage.exceptions.*;
import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;
import com.smart.garage.models.dtos.VehicleREST;
import com.smart.garage.services.contracts.VehicleService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.VehicleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public VehicleController(VehicleService vehicleService, VehicleMapper vehicleMapper,
                             AuthenticationHelper authenticationHelper) {
        this.vehicleService = vehicleService;
        this.vehicleMapper = vehicleMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<Vehicle> getAll(@RequestParam(required = false) Optional<String> identifier,
                                @RequestParam(required = false) Optional<String> owner,
                                @RequestParam(required = false) Optional<String> make,
                                @RequestParam(required = false) Optional<String> model,
                                @RequestParam(required = false) Optional<Set<Integer>> year,
                                @RequestParam(required = false) Optional<String> sorting,
                                @RequestParam(required = false) Optional<String> order) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            return vehicleService.getAll(currentUser,
                    identifier, owner, make, model, year, sorting, order, true);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable int id) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            return vehicleService.getById(currentUser, id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/new")
    public Vehicle create(@Valid @RequestBody VehicleREST vehicleREST) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            Vehicle vehicle = vehicleMapper.toObject(currentUser, vehicleREST);
            return vehicleService.create(currentUser, vehicle);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalVehicleBrand e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}/update")
    public Vehicle update(@PathVariable int id, @Valid @RequestBody VehicleREST vehicleREST) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            Vehicle original = vehicleService.getById(currentUser, id);
            Vehicle modified = vehicleMapper.toObject(currentUser, vehicleREST, original);
            return vehicleService.update(currentUser, modified);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalVehicleBrand e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Vehicle delete(@PathVariable int id) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            vehicleService.softDelete(currentUser, id);
            return vehicleService.getById(currentUser, id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
