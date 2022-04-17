package com.smart.garage.utility;

import com.smart.garage.exceptions.InvalidParameter;

import java.util.*;
import java.util.stream.Collectors;

public class VehicleQueryMaker {

    private static final String GET_ALL = "SELECT vehicles.id, user_id, license_plate, vehicle_identification_number, year_of_creation, model_id, is_deleted FROM vehicles";
    private static final String BY_LICENSE_OR_VIN = "(LOWER(license_plate) LIKE LOWER(:identifier)) OR (LOWER(vehicle_identification_number) LIKE LOWER(:identifier))";

    private static final String BY_OWNER = "(user_id IN (:owner))";
    private static final String BY_MODEL_OR_MAKE = "(model_id IN (SELECT id FROM vehicle_models WHERE vehicle_models.name IN (:name) OR make_id IN (SELECT id FROM vehicle_makes WHERE vehicle_makes.name IN (:name))))";
    private static final String BY_YEAR = "(year_of_creation IN (:year))";

    private static final String JOIN_MAKE = " LEFT JOIN vehicle_models ml on vehicles.model_id = ml.id LEFT JOIN vehicle_makes mk on ml.make_id = mk.id ";
    private static final String JOIN_MODEL = " LEFT JOIN vehicle_models ml on vehicles.model_id = ml.id ";

    public static final String DEFAULT_ORDER_DESC = " ORDER BY vehicles.id DESC";

    private static final String ORDER_BY_MAKE_ASC = " ORDER BY LOWER(mk.name) ASC, LOWER(ml.name) ASC";
    private static final String ORDER_BY_MAKE_DESC = " ORDER BY LOWER(mk.name) DESC, LOWER(ml.name) DESC";

    private static final String ORDER_BY_MODEL_ASC = " ORDER BY LOWER(ml.name) ASC";
    private static final String ORDER_BY_MODEL_DESC = " ORDER BY LOWER(ml.name) DESC";

    private static final String ORDER_BY_YEAR_ASC = " ORDER BY year_of_creation ASC";
    private static final String ORDER_BY_YEAR_DESC = " ORDER BY year_of_creation DESC";

    private static final String INVALID_SORT = "Invalid sort request. Allowed parameters: 'make', 'model','year'.";
    private static final String INVALID_ORDER = "Invalid order request. Allowed parameters: 'asc' and 'desc'.";

    private final StringBuilder query;
    private final List<String> filters;
    private final Map<String, Object> params;

    public VehicleQueryMaker() {
        query = new StringBuilder(GET_ALL);
        filters = new ArrayList<>();
        params = new HashMap<>();
    }

    public void setFilterClause(Optional<String> identifier, Optional<Set<Integer>> owner,
                                Optional<Set<String>> brand, Optional<Set<Integer>> year) {
        filterWithPartialMatching(identifier, BY_LICENSE_OR_VIN, "identifier");
        filterWithExactMatching(owner, BY_OWNER, "owner");
        filterWithExactMatching(formatNames(brand), BY_MODEL_OR_MAKE, "name");
        filterWithExactMatching(year, BY_YEAR, "year");

        if (!filters.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", filters));
        }
    }

    public void setSortClause(Optional<String> sorting, Optional<String> order) {
        if (sorting.isEmpty() && order.isPresent()) setDefaultSort(order);

        if (sorting.isPresent()) {
            String sortBy = sorting.get();
            validateSortParameter(sortBy);
            if (sortBy.equalsIgnoreCase("make")) {
                joinAndSort(order, JOIN_MAKE, ORDER_BY_MAKE_ASC, ORDER_BY_MAKE_DESC);
            } else if (sortBy.equalsIgnoreCase("model")) {
                joinAndSort(order, JOIN_MODEL, ORDER_BY_MODEL_ASC, ORDER_BY_MODEL_DESC);
            } else {
                if (order.isEmpty() || order.get().equalsIgnoreCase("asc")) query.append(ORDER_BY_YEAR_ASC);
                else if (order.get().equalsIgnoreCase("desc")) query.append(ORDER_BY_YEAR_DESC);
                else throw new InvalidParameter(INVALID_ORDER);
            }
        }
    }

    public void excludeDeleted() {
        filters.add("is_deleted = (:nonDeleted)");
        params.put("nonDeleted", 0);
    }

    public String getQuery() {
        return query.toString();
    }

    public Map<String, Object> getParams() {
        return params;
    }

    private Optional<Set<String>> formatNames(Optional<Set<String>> brand) {
        return brand.map(names -> names.stream()
                .map(name -> name.contains(" :: ")
                        ? name.split(" :: ")[1]
                        : name.replace(" (ALL)", ""))
                .collect(Collectors.toSet()));
    }

    private void filterWithPartialMatching(Optional<String> input, String name, String key) {
        input.ifPresent(value -> {
            filters.add(name);
            params.put(key, "%" + value + "%");
        });
    }

    private <T> void filterWithExactMatching(Optional<T> input, String name, String key) {
        input.ifPresent(value -> {
            filters.add(name);
            params.put(key, value);
        });
    }

    private void setDefaultSort(Optional<String> order) {
        if (order.get().equalsIgnoreCase("desc")) {
            query.append(DEFAULT_ORDER_DESC);
        } else if (!order.get().equalsIgnoreCase("asc")) {
            throw new InvalidParameter(INVALID_ORDER);
        }
    }

    private void validateSortParameter(String sortBy) {
        if (!(sortBy.equalsIgnoreCase("make") || sortBy.equalsIgnoreCase("model") || sortBy.equalsIgnoreCase("year"))) {
            throw new InvalidParameter(INVALID_SORT);
        }
    }

    private void joinAndSort(Optional<String> order, String joinTableName, String asc, String desc) {
        if (order.isEmpty() || order.get().equalsIgnoreCase("asc")) {
            if (query.toString().contains("WHERE")) query.insert(query.indexOf("WHERE"), joinTableName);
            else query.append(joinTableName);
            query.append(asc);
        } else if (order.get().equalsIgnoreCase("desc")) {
            if (query.toString().contains("WHERE")) query.insert(query.indexOf("WHERE"), joinTableName);
            else query.append(joinTableName);
            query.append(desc);
        } else throw new InvalidParameter(INVALID_ORDER);
    }
}
