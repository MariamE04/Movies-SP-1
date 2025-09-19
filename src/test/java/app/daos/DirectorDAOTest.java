package app.daos;

import app.config.HibernateConfig;
import app.entities.Director;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DirectorDAOTest {
    private static EntityManagerFactory emf;
    private static DirectorDAO dao;


    @BeforeEach
    void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest(); // brug test-DB
        dao = new DirectorDAO(emf);
    }

    @Test
    void creat() {
        Director d = Director.builder()
                .name("Test1")
                .directorId(123333)
                .job("Director")
                .department("Directing")
                .build();

        dao.creat(d);

        Director found =dao.getById(d.getDirectorId());

        assertNotNull(found);
        assertEquals("Test1", found.getName());

    }

    @Test
    void getById() {
        Director d = Director.builder()
                .name("Test2")
                .directorId(040404)
                .job("Director")
                .department("Directing")
                .build();

        dao.creat(d);

        Director found =dao.getById(d.getDirectorId());
        assertNotNull(found);

        assertEquals(d.getDirectorId(), found.getDirectorId());
        assertEquals("Test2", found.getName());

    }

    @Test
    void update() {
        Director d = Director.builder()
                .name("Test3")
                .directorId(99999)
                .job("Director")
                .department("Directing")
                .build();

        dao.creat(d);

        d.setName("Mariam");
        dao.update(d);

        Director found = dao.getById(d.getDirectorId());
        assertEquals("Mariam", found.getName());

    }

    @Test
    void getAll() {
        // Vi tjekker hvor mange directors der er før indsættelse
        int before = dao.getAll().size();

        // Opret directors
        Director d = Director.builder()
                .name("Test1")
                .directorId(111111)
                .job("Director")
                .department("Directing")
                .build();

        Director d1 = Director.builder()
                .name("Test2")
                .directorId(23456)
                .job("Director")
                .department("Directing")
                .build();

        Director d2 = Director.builder()
                .name("Test3")
                .directorId(0)
                .job("Director")
                .department("Directing")
                .build();

        dao.creat(d);
        dao.creat(d1);
        dao.creat(d2);

        // Hent alle directors igen
        List<Director> found = dao.getAll();

        assertNotNull(found);

        // Forvent antal +3
        assertEquals(before + 3, found.size(), "there should be 3 more directors");

        // Tjek at de rigtige navne er der
        List<String> names = found.stream().map(Director::getName).toList();
        assertTrue(names.contains("Test1"));
        assertTrue(names.contains("Test2"));
        assertTrue(names.contains("Test3"));
    }


    @Test
    void delete() {
        // Opret directors
        Director d = Director.builder()
                .name("Test1")
                .directorId(111111)
                .job("Director")
                .department("Directing")
                .build();

        Director d1 = Director.builder()
                .name("Test2")
                .directorId(23456)
                .job("Director")
                .department("Directing")
                .build();

        dao.creat(d);
        dao.creat(d1);

        // Før sletning burde der være 2
        assertEquals(2, dao.getAll().size());

        dao.delete(d.getId());

        // Efter sletning burde der være 1
        assertEquals(1, dao.getAll().size());


    }

    @Test
    void findByName() {
        // 1 Opret nogle directors
        Director d1 = Director.builder()
                .name("Mariam")
                .directorId(111111)
                .job("Director")
                .department("Directing")
                .build();

        Director d2 = Director.builder()
                .name("Peter")
                .directorId(222222)
                .job("Director")
                .department("Directing")
                .build();

        dao.creat(d1);
        dao.creat(d2);

        // 2 Find en eksisterende director
        Optional<Director> found = dao.findByName("Mariam");
        assertTrue(found.isPresent(), "Director 'Mariam' should be found");
        assertEquals("Mariam", found.get().getName());
        assertEquals(d1.getDirectorId(), found.get().getDirectorId());

        // 3 Forsøger at finde en director der ikke findes
        //Optional<Director> notFound = dao.findByName("NonExistent");
        //assertTrue(notFound.isEmpty(), "Director 'NonExistent' should not be found");
    }


}