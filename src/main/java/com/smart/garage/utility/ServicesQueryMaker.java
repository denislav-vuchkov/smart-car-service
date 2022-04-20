package com.smart.garage.utility;

import com.smart.garage.exceptions.InvalidParameter;

import java.util.*;
import java.util.stream.Collectors;

public class ServicesQueryMaker {

    public static final String[] PERMITTED_SORT_CRITERIA = {"id", "name", "price_bgn"};

    private final StringBuilder query;
    private final List<String> filters;
    private final HashMap<String, Object> propertiesMap;

    public ServicesQueryMaker() {
        this.query = new StringBuilder(" from Servicez ");
        filters = new ArrayList<>();
        propertiesMap = new HashMap<>();
    }


    public String buildHQLSearchAndSortQuery(Optional<String> name, Optional<Integer> priceMinimum,
                                             Optional<Integer> priceMaximum, Optional<String> sortBy,
                                             Optional<String> sortOrder) {
        name.ifPresent(value -> {
            filters.add(String.format(" lower(%s) like lower(:%s) ", "name", "name"));
            propertiesMap.put("name", "%" + value + "%");
        });

        priceMinimum.ifPresent(value -> {
            filters.add(String.format(" %s >= :%s ", "priceBGN", "minimumPrice"));
            propertiesMap.put("minimumPrice", value);
        });

        priceMaximum.ifPresent(value -> {
            filters.add(String.format(" %s <= :%s ", "priceBGN", "maximumPrice"));
            propertiesMap.put("maximumPrice", value);
        });

        if (!filters.isEmpty()) {
            query.append(" where ").append(String.join(" and ", filters));
        }

        sortBy.ifPresent(value -> {
            query.append(addSortCriteria(sortBy, sortOrder));
        });

        return query.toString();
    }

    public HashMap<String, Object> getProperties() {
        return propertiesMap;
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
