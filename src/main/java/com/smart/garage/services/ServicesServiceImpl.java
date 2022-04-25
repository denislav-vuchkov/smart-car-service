package com.smart.garage.services;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.Servicez;
import com.smart.garage.models.User;
import com.smart.garage.models.UserRoles;
import com.smart.garage.repositories.contracts.ServicesRepository;
import com.smart.garage.services.contracts.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.smart.garage.utility.AuthenticationHelper.RESTRICTED_FOR_EMPLOYEES;

@Service
public class ServicesServiceImpl implements ServicesService {

 private final ServicesRepository servicesRepository;

    @Autowired
    public ServicesServiceImpl(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }

    @Override
    public List<Servicez> getAll(Optional<String> name, Optional<Integer> priceMinimum, Optional<Integer> priceMaximum,
                                 Optional<String> sortBy, Optional<String> sortOrder) {
        return servicesRepository.getAll(name, priceMinimum, priceMaximum, sortBy, sortOrder);
    }

    @Override
    public Servicez getMostExpensiveService() {
        return servicesRepository.getMostExpensiveService();
    }

    @Override
    public Servicez getById(int id) {
        validateID(id);
        return servicesRepository.getById(id);
    }

    @Override
    public Servicez create(User requester, Servicez service) {
        throwIfCustomer(requester);
        servicesRepository.verifyFieldIsUnique("name", service.getName());
        service.setName(formatServiceName(service.getName()));
        servicesRepository.create(service);
        return service;
    }

    @Override
    public Servicez update(User requester, Servicez service) {
        throwIfCustomer(requester);
        verifyFieldIsUniqueIfChanged("name", service.getName(), service.getId());
        servicesRepository.update(service);
        return service;
    }

    @Override
    public void delete(User requester, int id) {
        validateID(id);
        throwIfCustomer(requester);
        Servicez servicez = servicesRepository.getById(id);
        servicesRepository.delete(id);
    }

    private void validateID(int id) {
        if (id <= 0) throw new InvalidParameter("ID must be a positive number.");
    }

    private void throwIfCustomer(User user) {
        if (user.getRole().getId() == UserRoles.CUSTOMER.getValue()) {
            throw new UnauthorizedOperationException(RESTRICTED_FOR_EMPLOYEES);
        }
    }

    private void verifyFieldIsUniqueIfChanged(String field, String value, int id) {
        Servicez service = servicesRepository.getBySpecificField(field, value);

        if (service != null && service.getId() != id) {
            throw new DuplicateEntityException("Service", field, value);
        }
    }

    private String formatServiceName(String name) {
        String[] words = name.split(" ");

        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].toUpperCase();
            char[] symbols = words[i].toCharArray();

            for (int j = 1; j < symbols.length; j++) {
                if (Character.isAlphabetic(symbols[j]) && Character.isUpperCase(symbols[j])) {
                    symbols[j] += 32;
                }
            }

            StringBuilder updatedWord = new StringBuilder();
            Stream.of(symbols).forEach(updatedWord::append);

            words[i] = updatedWord.toString();
        }

        return String.join(" ", words);
    }
}
