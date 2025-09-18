package app.daos;

import app.config.HibernateConfig;
import app.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ActorDAO implements IDAO<Actor,Integer>{
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public ActorDAO(EntityManagerFactory emf){
        this.emf = emf;
    }



    @Override
    public Actor creat(Actor actor) {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(actor);
            em.getTransaction().commit();
        }
        return actor;
    }

    @Override
    public Actor getById(Integer id) {

        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Actor actor = em.find(Actor.class, id);
            em.getTransaction().commit();

            return actor;
        }
    }

    @Override
    public Actor update(Actor actor) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.merge(actor);
            em.getTransaction().commit();
        }
        return null;
    }

    @Override
    public List<Actor> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT a FROM Actor a", Actor.class)
                    .getResultList();

        }
    }

    @Override
    public boolean delete(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            Actor actor = em.find(Actor.class, id);
            if(actor != null){
                em.getTransaction().begin();
                em.remove(actor);
                em.getTransaction().commit();
                return true;
            }else {
                System.out.println("Fejl med at fjerne Actor fra databasen:" );
                em.getTransaction().rollback();
                return false;
            }
        }

    }

    public Actor getByActorId(int actorId){
        try(EntityManager em = emf.createEntityManager()) {


            return em.createQuery("SELECT a FROM Actor a LEFT JOIN FETCH a.movieCasts WHERE a.actorId = :actorId", Actor.class)
                    .setParameter("actorId", actorId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }
}
