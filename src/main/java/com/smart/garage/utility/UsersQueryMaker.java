package com.smart.garage.utility;

import com.smart.garage.exceptions.InvalidParameter;

import java.util.*;
import java.util.stream.Collectors;

public class UsersQueryMaker {

    public static final String[] PERMITTED_SORT_CRITERIA = {"username", "u.id", "phone_number", "email"};
    private static final String GET_ALL = "select * " +
            "    from users as u\n" +
            "    left join vehicles v on u.id = v.user_id\n" +
            "    left join vehicle_models vm on v.model_id = vm.id\n" +
            "    left join vehicle_makes m on vm.make_id = m.id\n " +
            "    left join roles r on r.id = u.role_id ";

    private static final String BY_LICENSE_OR_VIN = "(v.license_plate like :licenseOrVIN or v.vehicle_identification_number like :licenseOrVIN)";


    private final StringBuilder query;
    private final List<String> filters;
    private final HashMap<String, Object> propertiesMap;

    public UsersQueryMaker(int roleID) {
        query = new StringBuilder(GET_ALL);
        query.append(" where r.id = ").append(roleID).append(" ");
        propertiesMap = new HashMap<>();
        filters = new ArrayList<>();
    }


    public String buildSQLSearchAndSortQuery(Optional<String> username, Optional<String> email, Optional<String> phoneNumber,
                                             Optional<String> licenseOrVIN, Optional<String> make,
                                             Optional<String> model, Optional<String> sortBy, Optional<String> sortOrder) {
        buildSearch("username", username);
        buildSearch("email", email);
        buildSearch("phone_number", phoneNumber);
        buildSearch("m.name", make);
        buildSearch("vm.name", model);

        licenseOrVIN.ifPresent(value -> {
            filters.add(BY_LICENSE_OR_VIN);
            propertiesMap.put("licenseOrVIN", "%" + value + "%");
        });

        if (!filters.isEmpty()) {
            query.append(" and ").append(String.join(" and ", filters));
        }

        query.append(" group by u.username ");

        if (sortBy.isPresent()) {
            query.append(addSortCriteria(sortBy, sortOrder));
        } else {
            query.append(" order by u.id");
        }

        return query.toString();
    }

    public HashMap<String, Object> getPropertiesMap() {
        return propertiesMap;
    }

    private void buildSearch(String column, Optional<String> parameter) {
        parameter.ifPresent(value -> {
            filters.add(String.format(" %s like :%s", column, column));
            propertiesMap.put(column, "%" + value + "%");
        });
    }

    private String addSortCriteria(Optional<String> sortBy, Optional<String> sortOrder) {
        if (sortBy.isEmpty()) {
            return "";
        }

        StringBuilder addToQuery = new StringBuilder(" order by ");

        if (!checkIfValidSortCriteria(sortBy)) {
            throw new InvalidParameter(String.format("The sort criteria chosen is not a valid one. Please " +
                    "enter one of the following criteria: %s.", String.join(", ", PERMITTED_SORT_CRITERIA)));
        }

        addToQuery.append(sortBy.get());

        if (sortOrder.isEmpty() || sortOrder.get().equals("asc")) {
            return addToQuery.toString();
        } else if (sortOrder.get().equalsIgnoreCase("desc")) {
            addToQuery.append(" desc");
        } else {
            throw new InvalidParameter("Invalid sort order has been selected. You can only select 'asc' or 'desc' " +
                    "sort order. If nothing is selected results will be returned in ascending order.");
        }

        return addToQuery.toString();
    }

    private boolean checkIfValidSortCriteria(Optional<String> sortBy) {
        List<String> result = Arrays.stream(PERMITTED_SORT_CRITERIA)
                .filter(e -> e.equals(sortBy.get()))
                .collect(Collectors.toList());

        return !result.isEmpty();
    }

}
