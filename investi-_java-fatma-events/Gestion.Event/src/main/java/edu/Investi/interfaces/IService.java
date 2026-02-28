package edu.Investi.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    void addEntity(T t) throws SQLException;
    void deleteEntity(int id) throws SQLException;
    void updateEntity(T t) throws SQLException;
    List<T> getData() throws SQLException;
    T getById(int id) throws SQLException;
    boolean exists(int id) throws SQLException;
}