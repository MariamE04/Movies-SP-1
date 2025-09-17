package app.daos;

import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MovieDAO implements IDAO <Movie, Integer> {

    private final EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Movie creat(Movie movie) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            em.persist(movie);

            em.getTransaction().commit();
        }
        return movie;
    }

    @Override
    public Movie getById(Integer integer) {
        return null;
    }

    @Override
    public Movie update(Movie movie) {
        return null;
    }

    @Override
    public List<Movie> getAll() {
        return List.of();
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }


}
