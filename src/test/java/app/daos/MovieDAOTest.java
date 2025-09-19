package app.daos;

import app.config.HibernateConfig;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDAOTest {

    private static EntityManagerFactory emf;
    private static MovieDAO movieDAO;

    @BeforeEach
    void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        movieDAO = new MovieDAO(emf);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createAndGetById() {
        Movie m = Movie.builder()
                .title("Morderen")
                .build();

        movieDAO.creat(m);

        Movie a = movieDAO.getById(m.getId());

        assertEquals("Morderen", a.getTitle());


    }


    @Test
    void update() {

        Movie movie = Movie.builder()
                .title("Skoven")
                        .build();

        movie.setTitle("Regnen");


        movieDAO.update(movie);
        assertEquals("Regnen", movie.getTitle());
    }

    @Test
    void getAll() {

        Movie movie = Movie.builder()
                .title("Mudder")
                .build();
        Movie movie1 = Movie.builder()
                .title("Blade")
                .build();


        movieDAO.creat(movie);
        movieDAO.creat(movie1);

        List<Movie> movies = movieDAO.getAll();


        assertEquals(2, movies.size());

    }

    @Test
    void delete() {
        Movie movie = Movie.builder()
                .title("Mudder")
                .build();
        Movie movie1 = Movie.builder()
                .title("Blade")
                .build();


        movieDAO.creat(movie);
        movieDAO.creat(movie1);

        movieDAO.delete(2);
        List<Movie> movies = movieDAO.getAll();


        assertEquals(1, movies.size());


    }

    @Test
    void findByTitleContainingIgnoreCase() {
        Movie movie = Movie.builder()
                .title("Mudder")
                .build();
        movieDAO.creat(movie);

        List<Movie> found = movieDAO.findByTitleContainingIgnoreCase("mudder");

        assertEquals(1, found.size());
        System.out.println(found);
    }

    @Test
    void findByGenreName() {
    }

    @Test
    void getAverageRating() {
    }

    @Test
    void getTop10HighestRated() {
    }

    @Test
    void getTop10LowestRated() {
    }

    @Test
    void getTop10MostPopular() {
    }
}