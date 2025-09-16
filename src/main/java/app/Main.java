package app;

import app.config.HibernateConfig;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.services.DirectorService;
import app.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        System.out.println("test");


        /*MovieService movieService = new MovieService();

        // Hent listen af film
        List<MovieDTO> movies = movieService.getMovieInfo();

        // Loop gennem og print hver film
        for (MovieDTO movie : movies) {
            System.out.println("Titel: " + movie.getTitle());
            System.out.println("Udgivelsesdato: " + movie.getRelease_date());
            System.out.println("Sprog: " + movie.getOriginal_language());
            System.out.println("Popularity: " + movie.getPopularity());
            System.out.println("Rating: " + movie.getVote_average());
            System.out.println("-----------------------------------");
        }*/

        DirectorService directorService = new DirectorService();

        // Hent directors for en film
        List<DirectorDTO> directors = directorService.getDirectorsByMovieId(1426672);

        for (DirectorDTO d : directors) {
            System.out.println("Director: " + d.getName() + " (TMDb ID: " + d.getId() + ")");
        }



    }
}