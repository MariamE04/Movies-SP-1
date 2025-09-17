package app.daos;

import app.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DirectorDAO implements IDAO<Director, Integer> {

    private final EntityManagerFactory  emf;

    public DirectorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Director creat(Director director) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            em.persist(director);

            em.getTransaction().commit();
        }
        return director;
    }

    @Override
    public Director getById(Integer directorId) {
        try(EntityManager em = emf.createEntityManager()){
           Director found = em.createQuery("SELECT d FROM Director d WHERE d.directorId =: id", Director.class)
                    .setParameter("id", directorId)
                    .getSingleResult();



            return found;
        }
    }

    @Override
    public Director update(Director director) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            em.persist(director);

            em.getTransaction().commit();

        }
        return director;
    }

    @Override
    public List<Director> getAll() {
        try(EntityManager em = emf.createEntityManager()){

            List<Director> directors = em.createQuery("SELECT d FROM Director d", Director.class)
                    .getResultList();

            return directors;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Director delete = em.find(Director.class, id);
            if (delete != null) {
                em.remove(delete);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }

        }
    }
}
