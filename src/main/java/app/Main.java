package app;

import app.dtos.MovieDTO;
import app.services.MovieService;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Filmtest ===");

        MovieService movieService = new MovieService();


        // 1 Hent listen af danske film
        List<MovieDTO> movies = movieService.getMovieInfo();

      //  movieService.MoviesWithActors(movies);


    }
}
