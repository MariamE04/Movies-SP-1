package app.daos;

import app.entities.MovieGenre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class MovieGenreDAO {

    private final EntityManagerFactory emf;

    public MovieGenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public MovieGenre create(MovieGenre mg) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(mg);
            em.getTransaction().commit();
            return mg;
        }
    }
}