package app.daos;

import app.config.HibernateConfig;
import app.entities.Actor;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ActorDAOTest {

    private static EntityManagerFactory emf;
    private static ActorDAO actorDAO;

    @BeforeAll
    static void setUpDataBase(){
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        actorDAO = new ActorDAO(emf);
    }

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void creatAndFindById() {
        Actor actor = new Actor();
        actor.setName("Asger Junker");
        actor.setDepartment("Toilet");

        actorDAO.creat(actor);

        assertEquals("Asger Junker", actor.getName());

        //actorDAO.getById(1);
        assertNotNull(actorDAO.getById(1));

    }


    @Test
    void update() {
        Actor actor = new Actor();

        actor.setName("Jimmy Dean");
        assertEquals("Jimmy Dean", actor.getName());
        actor.setName("Jimmy hendricks");
        actorDAO.update(actor);
        assertEquals("Jimmy hendricks", actor.getName());
    }

    @Test
    void getAll() {
        List<Actor> actors = actorDAO.getAll();

        actors.add(Actor.builder()
                        .name("Jimmy Guy")
                .build());

        actors.add(Actor.builder()
                        .name("Ole lukøje")
                .build());


        assertEquals(2, actors.size());
    }

    @Test
    void delete() {
        Actor actor = new Actor();
        actor.setName("Ida Sørensen");

        actorDAO.creat(actor);


        boolean delete = actorDAO.delete(actor.getId());
        assertTrue(delete);

        Actor found = actorDAO.getById(actor.getId());
        assertNull(found);
    }

    @Test
    void getByActorId() {
        Actor actor = new Actor();
        actor.setName("Arne NuggatGren");
        actor.setActorId(1);

        actorDAO.getByActorId(1);
        assertEquals(1, actor.getActorId());
        System.out.println(actor);
    }
}