package com.smart.garage.repositories.contracts;

import java.util.List;

public interface BaseReadRepository<T> {

    List<T> getAll();

    T getById(int id);

    <V> T getByField(String name, V value);

    void verifyFieldIsUnique(String field, String value);

    T getBySpecificField(String field, String value);

}
