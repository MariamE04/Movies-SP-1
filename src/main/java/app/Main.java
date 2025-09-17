package app;


import app.dtos.MovieDTO;
import app.services.ApiServices;
import app.services.MovieService;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Filmtest ===");


        MovieService movieService = new MovieService();

        // 1 Hent listen af danske film
        List<MovieDTO> movies = movieService.getMovieInfo();

        // 2️ Tilføj directors og gem dem i DB
        movieService.MoviesWithDirectors(movies);

    }
}
