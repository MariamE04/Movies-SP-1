package app.daos;

import java.util.List;

public interface IDAO<T,I> {

    T creat(T t);
    T getById(I i);
    T update(T t);
    List<T> getAll();
    boolean delete(I i);
}
