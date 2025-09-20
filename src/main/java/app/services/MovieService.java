package app.services;

import app.config.HibernateConfig;
import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.daos.MovieGenreDAO;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;

import app.entities.*;

import app.mappers.ActorMapper;
import app.mappers.DirectorMapper;
import app.mappers.GenreMapper;
import app.mappers.MovieMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MovieService {

    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    private ApiServices apiServices = new ApiServices();
    private JsonToDTOConverters jsonToDTOConverters = new JsonToDTOConverters();

    public List<MovieDTO> getMovieInfo() {
        String apiKey = System.getenv("API_KEY");
        String uri = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&with_origin_country=DK&primary_release_date.gte=2020-01-01&primary_release_date.lte=2025-12-31&sort_by=popularity.desc";

        //Opretter en tom liste, som skal samle alle film fra alle sider.
        List<MovieDTO> allMovies = new ArrayList<>();

        int currentPage = 1; //Starter på side 1
        int totalPages = 1; //bliver opdateret senere (når vi ved hvor mange sider der er)

        do { //vi kører mindst én gang, uanset hvad.
            String json = apiServices.fetchFromApi(uri + "&page=" + currentPage); //henter JSON fra API’et for den aktuelle side (URL’en bygges dynamisk: ...&page=1)

            // Konverterer JSON-svaret fra den side til en liste af MovieDTO-objekter.
            List<MovieDTO> moviesPage = jsonToDTOConverters.toMovieDTOs(json);
            allMovies.addAll(moviesPage); // Tilføjer filmene fra denne side til den samlede liste

            // Sæt totalPages første gang
            if (currentPage == 1) {
                totalPages = jsonToDTOConverters.extractTotalPages(json);
            }

            currentPage++; //Går videre til næste side
        } while (currentPage <= totalPages); //Løkken fortsætter indtil vi har hentet alle sider (fra 1 til totalPages).

        return allMovies; // returner hele listen, ikke kun første side
    }

    public void MoviesWithDirectors(List<MovieDTO> movies) throws InterruptedException {
        DirectorService directorService = new DirectorService();
        GenreService genreService = new GenreService();
        DirectorDAO directorDAO = new DirectorDAO(emf);
        MovieDAO movieDAO = new MovieDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);
        MovieGenreDAO movieGenreDAO = new MovieGenreDAO(emf);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (MovieDTO movieDTO : movies) {
            executor.submit(() -> {
                // Hent director og genre fra API
                List<DirectorDTO> directors = directorService.getDirectorsByMovieId(movieDTO.getId());
                if (directors.isEmpty()) return;

                DirectorDTO directorDTO = directors.get(0);
                movieDTO.setDirectorDTO(directorDTO);
                List<GenreDTO> genres = genreService.getGenreInfo(movieDTO.getId());
                movieDTO.setGenreDTO(genres);

                // Brug én EntityManager / transaction per film
                try (var em = emf.createEntityManager()) {
                    var tx = em.getTransaction();
                    tx.begin();

                    // --- Director ---
                    Director directorEntity = em.find(Director.class, directorDTO.getId());
                    if (directorEntity == null) {
                        directorEntity = DirectorMapper.toEntity(directorDTO);
                        em.persist(directorEntity);
                    }

                    // --- Movie ---
                    Movie movieEntity = MovieMapper.toEntity(movieDTO);
                    movieEntity.setDirector(directorEntity);
                    em.persist(movieEntity);

                    // --- Genre og MovieGenre ---
                    for (GenreDTO genreDTO : genres) {
                        Genre genreEntity = em.find(Genre.class, genreDTO.getId());
                        if (genreEntity == null) {
                            genreEntity = GenreMapper.toEntity(genreDTO);
                            em.persist(genreEntity);
                        }

                        MovieGenre mg = new MovieGenre();
                        mg.setMovie(movieEntity);
                        mg.setGenre(genreEntity);
                        em.persist(mg); // Persist i samme transaction

                        movieEntity.getMovieGenres().add(mg); // memory-relation
                    }

                    tx.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
    }


    public void MoviesWithActors(List<MovieDTO> movies) throws InterruptedException {
        ActorService actorService = new ActorService();
        ActorDAO actorDAO = new ActorDAO(emf);
        MovieDAO movieDAO = new MovieDAO(emf);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for(MovieDTO movieDTO : movies){
            executor.submit(() ->{
                try{
                    List<ActorDTO> actorDTOS = actorService.getActorInfo(movieDTO.getId());
                    Movie movie = MovieMapper.toEntity(movieDTO);

                    for(ActorDTO actorDTO : actorDTOS){
                        Actor actor = actorDAO.getByActorId(actorDTO.getActor_id());

                        if(actor == null){
                            actor = ActorMapper.toEntity(actorDTO);
                            actorDAO.creat(actor);
                        }

                        for(String characterName : actorDTO.getCharacter()){
                            MovieCast movieCast = new MovieCast();

                            movieCast.setActor(actor);
                            movieCast.setMovie(movie);
                            movieCast.setCharacterName(characterName);

                            movie.getMoviesCasts().add(movieCast);
                            actor.getMovieCasts().add(movieCast);
                        }
                    }
                    movieDAO.creat(movie);
                } catch (Exception e){
                    e.printStackTrace();
                }
            });

        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

    }
}

