package app.daos;

import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GenreDAO implements IDAO<Genre, Integer> {

    private final EntityManagerFactory emf;

    public GenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Genre creat(Genre genre) {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(genre);
            em.getTransaction().commit();
            return genre;
        }
    }

    @Override
    public Genre getById(Integer genreId) {
        try(EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT g FROM Genre g WHERE g.genreId = ?1",Genre.class)
                    .setParameter(1, genreId)
                    .getSingleResult();
        }
    }

    @Override
    public Genre update(Genre genre) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Genre updatedgenre = em.merge(genre);
            em.getTransaction().commit();
            return updatedgenre;
        }

    }

    @Override
    public List<Genre> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT g FROM Genre g ", Genre.class)
                    .getResultList();
        }
    }

    @Override
    public boolean delete(Integer genreId) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
           int rowsAffected = em.createQuery("DELETE FROM Genre g where g.genreId = ?1 ")
                    .setParameter(1, genreId)
                    .executeUpdate();
           em.getTransaction().commit();
           //Hvis mere end 0 rækker er blevet ændret så return
           return rowsAffected > 0;
        }
    }
}
