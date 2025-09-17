package app.daos;

import app.entities.Genre;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GenreDAO implements IDAO<Genre, Integer> {

    private final EntityManagerFactory emf;

    public GenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Genre creat(Genre genre) {
        return null;
    }

    @Override
    public Genre getById(Integer integer) {
        return null;
    }

    @Override
    public Genre update(Genre genre) {
        return null;
    }

    @Override
    public List<Genre> getAll() {
        return List.of();
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }
}
