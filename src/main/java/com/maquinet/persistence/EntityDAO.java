package com.maquinet.persistence;

import java.util.List;

/**
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public interface EntityDAO<T>
{
    T get(int id);
    List<T> findAll();
    T findFirst();
    boolean saveAll(List<T> entities);
    boolean save(T entity);

    boolean delete(T entity);
}
