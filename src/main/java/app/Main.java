package app;

import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.services.DirectorService;
import app.services.GenreService;
import app.services.MovieService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Filmtest ===");

        MovieService movieService = new MovieService();

        // Hent filmene
        List<MovieDTO> movies = movieService.getMovieInfo();

        // Knyt directors til filmene
        movieService.MoviesWithDirectors(movies);

        // GenreService til at hente genrer
        GenreService genreService = new GenreService();

        // Print info om hver film
        for (MovieDTO movie : movies) {
            System.out.println("Titel: " + movie.getTitle());
            System.out.println("Original titel: " + movie.getOriginal_title());
            System.out.println("Sprog: " + movie.getOriginal_language());
            System.out.println("Udgivelsesdato: " + movie.getRelease_date());
            System.out.println("Beskrivelse: " + movie.getOverview());
            System.out.println("Popularity: " + movie.getPopularity());
            System.out.println("Rating: " + movie.getVote_average());
            System.out.println("Stemmer: " + movie.getVote_count());

            // Instruktør
            if (movie.getDirectorDTO() != null) {
                System.out.println("Instruktør: " + movie.getDirectorDTO().getName());
            } else {
                System.out.println("Instruktør: Ikke fundet");
            }

            // Genrer
            List<GenreDTO> genres = genreService.getGenreInfo(movie.getId());
            if (genres != null && !genres.isEmpty()) {
                System.out.print("Genrer: ");
                for (GenreDTO g : genres) {
                    System.out.print(g.getName() + " ");
                }
                System.out.println();
            } else {
                System.out.println("Genrer: Ikke fundet");
            }

            System.out.println("-----------------------------------");
        }
    }
}
