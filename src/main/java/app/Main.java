package app;

import app.config.HibernateConfig;
import app.dtos.MovieDTO;

import app.entities.Movie;
import app.services.MovieService;
import app.daos.MovieDAO;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Filmtest ===");

        MovieService movieService = new MovieService();


        // 1 Hent listen af danske film
        List<MovieDTO> movies = movieService.getMovieInfo();


        // 2️ Tilføj directors og gem dem i DB
        movieService.processor(movies);

       //movieService.MoviesWithActors(movies);
/*
        // Test af MovieDAO operationer
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        MovieDAO movieDAO = new MovieDAO(emf);


        System.out.println("Average rating for movieId 1: "+movieDAO.getAverageRating(1));


            List <Movie> movies1 = movieDAO.getTop10HighestRated();
        System.out.println("\nTop 10 højeste rated -----------");
            movies1.forEach(m -> System.out.println(m));

            movies1 = movieDAO.getTop10LowestRated();
        System.out.println("\nTop 10 laveste rated movies");
            movies1.forEach(m -> System.out.println(m));

            movies1 = movieDAO.getTop10MostPopular();
        System.out.println("\nTop 10 meste populærer movies: " );
            movies1.forEach(m -> System.out.println(m));


 */


    }
}
