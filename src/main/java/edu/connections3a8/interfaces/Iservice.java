package edu.connections3a8.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface Iservice<T>
{
    void addEntity(T entity) throws SQLException;
    void addEntity2(T entity) throws SQLException;
    void deleteEntity(T entity);
    void updateEntity(T entity,int id);
    List<T> getData();


}

