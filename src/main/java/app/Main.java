package app;

import app.dtos.MovieDTO;

import app.entities.Director;
import app.entities.Movie;
import app.services.DirectorService;
import app.services.GenreService;
import app.services.MovieService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Filmtest ===");

        MovieService movieService = new MovieService();


        // 1 Hent listen af danske film
        List<MovieDTO> movies = movieService.getMovieInfo();


        // 2️ Tilføj directors og gem dem i DB

        movieService.MoviesWithDirectors(movies);

      //  movieService.MoviesWithActors(movies);

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



    }
}
