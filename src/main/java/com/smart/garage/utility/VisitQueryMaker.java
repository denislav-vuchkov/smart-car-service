package com.smart.garage.utility;

import com.smart.garage.exceptions.InvalidParameter;

import java.time.LocalDateTime;
import java.util.*;

public class VisitQueryMaker {

    private static final String GET_ALL = "SELECT * FROM visits";
    private static final String BY_OWNER = "(user_id IN (:owner))";
    private static final String BY_VEHICLE = "(vehicle_id IN (:vehicle))";
    private static final String BY_STATUS = "status_id = :status";
    private static final String BY_START_DATE = "start_date >= :startDate";
    private static final String BY_END_DATE = "start_date <= :endDate";

    public static final String DEFAULT_ORDER_ASC = " ORDER BY visits.id DESC";
    public static final String DEFAULT_ORDER_DESC = " ORDER BY visits.id ASC";

    private static final String ORDER_BY_STATUS_ASC = " ORDER BY status_id ASC";
    private static final String ORDER_BY_STATUS_DESC = " ORDER BY status_id DESC";

    private static final String ORDER_BY_START_DATE_ASC = " ORDER BY start_date ASC";
    private static final String ORDER_BY_START_DATE_DESC = " ORDER BY start_date DESC";

    // TODO sort by ID when null + test that everything works
    private static final String ORDER_BY_END_DATE_ASC = " ORDER BY (CASE WHEN end_date IS NULL THEN 1 ELSE 0 END), end_date, id";
    private static final String ORDER_BY_END_DATE_DESC = " ORDER BY (CASE WHEN end_date IS NULL THEN 1 ELSE 0 END) DESC, end_date DESC, id DESC";

    private static final String INVALID_SORT = "Invalid sort request. Allowed parameters: 'status', 'start_date','end_date'.";
    private static final String INVALID_ORDER = "Invalid order request. Allowed parameters: 'asc' and 'desc'.";

    private final StringBuilder query;
    private final List<String> filters;
    private final Map<String, Object> params;

    public VisitQueryMaker() {
        query = new StringBuilder(GET_ALL);
        filters = new ArrayList<>();
        params = new HashMap<>();
    }

    public void setFilterClause(Optional<Set<Integer>> owner, Optional<Set<Integer>> vehicle, Optional<String> status,
                                Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate) {
        filterWithExactMatching(owner, BY_OWNER, "owner");
        filterWithExactMatching(vehicle, BY_VEHICLE, "vehicle");
        filterWithExactMatching(status, BY_STATUS, "status");
        filterWithDate(startDate, BY_START_DATE, "startDate");
        filterWithDate(endDate, BY_END_DATE, "endDate");

        if (!filters.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", filters));
        }
    }

    public void setSortClause(Optional<String> sorting, Optional<String> order) {
        if (sorting.isEmpty()) setDefaultSort(order);

        if (sorting.isPresent()) {
            String sortBy = sorting.get();
            validateSortParameter(sortBy);
            applySortIfSelected(sortBy, "status", order, ORDER_BY_STATUS_ASC, ORDER_BY_STATUS_DESC);
            applySortIfSelected(sortBy, "start_date", order, ORDER_BY_START_DATE_ASC, ORDER_BY_START_DATE_DESC);
            applySortIfSelected(sortBy, "end_date", order, ORDER_BY_END_DATE_ASC, ORDER_BY_END_DATE_DESC);
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

    private <T> void filterWithExactMatching(Optional<T> input, String name, String key) {
        input.ifPresent(value -> {
            filters.add(name);
            params.put(key, value);
        });
    }

    private void filterWithDate(Optional<LocalDateTime> input, String name, String key) {
        input.ifPresent(value -> {
            filters.add(name);
            params.put(key, value.toLocalDate().toString());
        });
    }

    private void setDefaultSort(Optional<String> order) {
        if (order.isEmpty() || order.get().equalsIgnoreCase("asc")) {
            query.append(DEFAULT_ORDER_ASC);
        } else if (order.get().equalsIgnoreCase("desc")) {
            query.append(DEFAULT_ORDER_DESC);
        } else {
            throw new InvalidParameter(INVALID_ORDER);
        }
    }

    private void validateSortParameter(String sortBy) {
        if (!(sortBy.equalsIgnoreCase("status") || sortBy.equalsIgnoreCase("start_date") ||
                sortBy.equalsIgnoreCase("end_date"))) {
            throw new InvalidParameter(INVALID_SORT);
        }
    }

    private void applySortIfSelected(String sortBy, String field, Optional<String> order, String ASC, String DESC) {
        if (sortBy.equalsIgnoreCase(field)) {
            if (order.isEmpty() || order.get().equalsIgnoreCase("asc"))
                query.append(ASC);
            else if (order.get().equalsIgnoreCase("desc"))
                query.append(DESC);
            else throw new InvalidParameter(INVALID_ORDER);
        }
    }
}
