package app.daos;

import app.config.HibernateConfig;
import app.entities.Genre;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GenreDAOTest {

    private static EntityManagerFactory emf;
    private static GenreDAO genreDAO;


    @BeforeEach
    void setUp() {
       emf = HibernateConfig.getEntityManagerFactoryForTest();
       genreDAO = new GenreDAO(emf);
        }

    @Test
    void creat() {
     Genre g = Genre.builder()
             .name("Action")
             .genreId(20)
             .build();

     genreDAO.creat(g);

     if(g != null){
        assertEquals("Action", genreDAO.getById(20).getName());
     }


    }

    @Test
    void getById() {
        Genre g = Genre.builder()
                .name("Gyser")
                .genreId(577)
                .build();

        genreDAO.creat(g);

        assertNotNull(g);
        assertEquals(577,g.getGenreId());
    }

    @Test
    void update() {
        Genre g = Genre.builder()
                .name("Eventyr")
                .genreId(20)
                .build();

        genreDAO.creat(g);

        assertNotNull(g);

        g.setGenreId(60);
        genreDAO.getAll().forEach(genreFromDB -> System.out.println("Før update :"+genreFromDB.getGenreId()));
        genreDAO.update(g);
        genreDAO.getAll().forEach(genreFromDB -> System.out.println("Efter update :"+genreFromDB.getGenreId()));
        assertEquals(60, g.getGenreId());

    }

    @Test
    void getAll() {
        Genre g1 = Genre.builder()
                .name("Sci-Fi")
                .genreId(20)
                .build();

        Genre g2 = Genre.builder()
                .name("Gyser")
                .genreId(30)
                .build();
        Genre g3 = Genre.builder()
                .name("Fantasy")
                .genreId(50)
                .build();
        genreDAO.creat(g1);
        genreDAO.creat(g2);
        genreDAO.creat(g3);

        List<Genre> genres = genreDAO.getAll();

        assertEquals(3, genres.size());

    }

    @Test
    void delete() {
        Genre g1 = Genre.builder()
                .name("Sci-Fi")
                .genreId(20)
                .build();

        Genre g2 = Genre.builder()
                .name("Gyser")
                .genreId(30)
                .build();
        Genre g3 = Genre.builder()
                .name("Fantasy")
                .genreId(50)
                .build();
        genreDAO.creat(g1);
        genreDAO.creat(g2);
        genreDAO.creat(g3);

        genreDAO.delete(50);

        List<Genre> genres = genreDAO.getAll();



        assertEquals(2, genres.size());

    }
}
