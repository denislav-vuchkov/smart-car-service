package com.smart.garage.repositories.contracts;

public interface BaseCRUDRepository<T> extends BaseReadRepository<T>{

    void create(T entity);

    void update(T entity);

    void delete(int id);

}
