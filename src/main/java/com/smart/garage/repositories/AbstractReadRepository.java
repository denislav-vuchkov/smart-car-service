package com.smart.garage.repositories;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.repositories.contracts.BaseReadRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.lang.String.format;

public abstract class AbstractReadRepository<T> implements BaseReadRepository<T> {

    private final Class<T> clazz;

    @Autowired
    protected SessionFactory sessionFactory;

    public AbstractReadRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Retrieves an entity from the database that has a <code>field</code> equal to <code>value</code>.
     * <br/>
     * Example: <code>getByField("id", 1, Parcel.class)</code>
     * will execute the following HQL: <code>from Parcel where id = 1;</code>
     *
     * @param name  the name of the field
     * @param value the value of the field
     * @return an entity that matches the given criteria
     */
    public <V> T getByField(String name, V value) {
        final String query = format("from %s where %s = :value", clazz.getName(), name);
        final String notFoundErrorMessage = format("%s with %s %s not found.", clazz.getSimpleName(), name, value);
        try (Session session = sessionFactory.openSession()) {
            return session
                    .createQuery(query, clazz)
                    .setParameter("value", value)
                    .uniqueResultOptional()
                    .orElseThrow(() -> new EntityNotFoundException(notFoundErrorMessage));
        }
    }

    @Override
    public T getById(int id) {
        return getByField("id", id);
    }

    @Override
    public List<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(format("from %s ", clazz.getName()), clazz).list();
        }
    }

    @Override
    public void verifyFieldIsUnique(String field, String value) {
        try (Session session = sessionFactory.openSession()) {
            String stringQuery = String.format(" from %s where %s = '%s' ", clazz.getSimpleName(), field, value);
            Query<T> query = session.createQuery(stringQuery, clazz);

            List<T> result = query.list();

            if (!result.isEmpty()) throw new DuplicateEntityException(clazz.getSimpleName(), field, value);
        }
    }

    public T getBySpecificField(String field, String value) {
        try (Session session = sessionFactory.openSession()) {
            String stringQuery = String.format(" from %s where %s = '%s' ", clazz.getSimpleName(),  field, value);
            Query<T> query = session.createQuery(stringQuery, clazz);

            List<T> result = query.list();

            if (result.isEmpty()) {
                return null;
            } else {
                return result.get(0);
            }
        }
    }

}
