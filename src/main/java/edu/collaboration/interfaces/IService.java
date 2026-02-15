package edu.collaboration.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
       void addEntity(T t) throws SQLException;

       void deleteEntity(T t);

       boolean update(int id, T t);

       List<T> getData();
}
