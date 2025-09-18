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

        // 2️ Tilføj directors og gem dem i DB
        movieService.MoviesWithDirectors(movies);

        // 3 Print film + director
       /* for (MovieDTO movie : movies) {
            System.out.println("Titel: " + movie.getTitle());
            System.out.println("Original titel: " + movie.getOriginal_title());
            System.out.println("Sprog: " + movie.getOriginal_language());
            System.out.println("Udgivelsesdato: " + movie.getRelease_date());
            System.out.println("Popularity: " + movie.getPopularity());
            System.out.println("Rating: " + movie.getVote_average());

            if (movie.getDirectorDTO() != null) {
                System.out.println("Director: " + movie.getDirectorDTO().getName() +
                        " (TMDb ID: " + movie.getDirectorDTO().getId() + ")");
            } else {
                System.out.println("Director: Ikke fundet");
            }

            System.out.println("-----------------------------------"); */

       //DirectorService directorService = new DirectorService();

        /*Optional<DirectorDTO> directorOpt = directorService.getDirectorByName("Mette Carla Albrechtsen");

        if (directorOpt.isPresent()) {
            System.out.println("Found by name: " + directorOpt.get());
        } else {
            System.out.println("Director not found");
        }*/

        //List<Director> list = directorService.getAllDirectors();
        //list.forEach(System.out::println);


    }
}
