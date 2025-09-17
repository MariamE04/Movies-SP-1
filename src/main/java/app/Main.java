package app;

import app.config.HibernateConfig;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.Director;
import app.services.DirectorService;
import app.services.GenreService;
import app.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Filmtest ===");

        MovieService movieService = new MovieService();


        // 1 Hent listen af danske film
        List<MovieDTO> movies = movieService.getMovieInfo();

        // 2️ Tilføj directors og gem dem i DB
        //movieService.MoviesWithDirectors(movies);

        movieService.MoviesWithActors(movies);

        //push
    }
}
