package app.services;

import app.config.HibernateConfig;
import app.daos.*;
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
        ActorService actorService = new ActorService();

        DirectorDAO directorDAO = new DirectorDAO(emf);
        MovieDAO movieDAO = new MovieDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);
        MovieGenreDAO movieGenreDAO = new MovieGenreDAO(emf);
        ActorDAO actorDAO = new ActorDAO(emf);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (MovieDTO movieDTO : movies) {
            executor.submit(() -> processMovie(movieDTO, directorService, genreService, actorService,
                    directorDAO, movieDAO, genreDAO, movieGenreDAO, actorDAO));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
    }

    // Hovedmetode for at processere én film
    private void processMovie(MovieDTO movieDTO, DirectorService directorService, GenreService genreService, ActorService actorService, DirectorDAO directorDAO, MovieDAO movieDAO, GenreDAO genreDAO, MovieGenreDAO movieGenreDAO, ActorDAO actorDAO) {

        // 1. Hent data fra API og sæt i MovieDTO
        List<DirectorDTO> directors = directorService.getDirectorsByMovieId(movieDTO.getId());
        if (directors.isEmpty()) return;

        DirectorDTO directorDTO = directors.get(0);
        movieDTO.setDirectorDTO(directorDTO);

        List<GenreDTO> genres = genreService.getGenreInfo(movieDTO.getId());
        movieDTO.setGenreDTO(genres);

        List<ActorDTO> actors = actorService.getActorInfo(movieDTO.getId());
        if (!actors.isEmpty()) {
            movieDTO.setActorDTO(actors.get(0));
            saveActorIfNotExists(actors.get(0), actorDAO);
        }

        // 2. Gem director i DB
        Director directorEntity = saveDirectorIfNotExists(directorDTO, directorDAO);

        // 3. Gem movie og relationer i DB
        saveMovieAndGenres(movieDTO, directorEntity, movieDAO, genreDAO, movieGenreDAO);
    }

    // Hjælpemetoder
    private void saveActorIfNotExists(ActorDTO actorDTO, ActorDAO actorDAO) {
        Actor actorEntity = actorDAO.getByActorId(actorDTO.getActor_id());
        if (actorEntity == null) {
            actorEntity = ActorMapper.toEntity(actorDTO);
            actorDAO.creat(actorEntity);
        }
    }

    private Director saveDirectorIfNotExists(DirectorDTO directorDTO, DirectorDAO directorDAO) {
        try {
            return directorDAO.getById(directorDTO.getId());
        } catch (Exception e) {
            Director directorEntity = DirectorMapper.toEntity(directorDTO);
            directorDAO.creat(directorEntity);
            return directorEntity;
        }
    }

    private void saveMovieAndGenres(MovieDTO movieDTO, Director directorEntity, MovieDAO movieDAO, GenreDAO genreDAO, MovieGenreDAO movieGenreDAO) {
        Movie movieEntity = MovieMapper.toEntity(movieDTO);
        movieEntity.setDirector(directorEntity);
        movieDAO.creat(movieEntity);

        for (GenreDTO genreDTO : movieDTO.getGenreDTO()) {
            Genre genreEntity;
            try {
                genreEntity = genreDAO.getById(genreDTO.getId());
            } catch (Exception e) {
                genreEntity = GenreMapper.toEntity(genreDTO);
                genreDAO.creat(genreEntity);
            }

            MovieGenre mg = new MovieGenre();
            mg.setMovie(movieEntity);
            mg.setGenre(genreEntity);
            movieGenreDAO.create(mg);
            movieEntity.getMovieGenres().add(mg);
        }
    }
}