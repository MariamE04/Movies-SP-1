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
    public Movie getById(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Movie movie = em.find(Movie.class, id);
            em.getTransaction().commit();
            return movie;
        }
    }

    @Override
    public Movie update(Movie movie) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.merge(movie);
            em.getTransaction().commit();
        }
        return movie;
    }

    @Override
    public List<Movie> getAll() {
        EntityManager em = emf.createEntityManager();

        return em.createQuery("SELECT m FROM Movie m", Movie.class)
                .getResultList();
    }

    @Override
    public boolean delete(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            Movie movie = em.find(Movie.class, id);
            if(movie != null){
                em.getTransaction().begin();
                em.remove(movie);
                em.getTransaction().commit();
                return true;
            }else {
                System.out.println("Problemer med at fjerne movie fra databasen");
                em.getTransaction().rollback();
                return false;
            }
        }
    }

    public List<Movie> findByTitleContainingIgnoreCase(String title){

        try(EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
                    .setParameter("title", title)
                    .getResultList()
                    ;
        }

    }

    public List<Movie> findByGenreName(String name){
        try(EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT m FROM Movie m " +
                    "JOIN MovieGenre mg ON m = mg.movie " +
                     "JOIN Genre g ON g = mg.genre " +
                    " WHERE g.name = :name", Movie.class)
                    .setParameter("name", name)
                    .getResultList();
        }
    }

    public double getAverageRating(int id){
        try(EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT m.vote_average FROM Movie m WHERE m.id = ?1", Double.class)
                    .setParameter(1, id)
                    .getSingleResult();
        }
    }

    public List<Movie> getTop10HighestRated(){
        try(EntityManager em = emf.createEntityManager()){
           return em.createQuery("SELECT m FROM Movie m ORDER BY m.vote_average desc", Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        }
    }

    public List<Movie> getTop10LowestRated(){
        try(EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT m FROM Movie m ORDER BY m.vote_average asc", Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        }
    }

    public List<Movie> getTop10MostPopular(){
        try(EntityManager em = emf.createEntityManager()){
            return em.createQuery("SELECT m FROM Movie m ORDER BY m.popularity desc", Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        }
    }



}
