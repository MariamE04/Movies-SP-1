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
        // Opret services til at hente data fra API’er
        DirectorService directorService = new DirectorService();
        GenreService genreService = new GenreService();
        ActorService actorService = new ActorService();

        // Opret DAO objekter til at kommunikere med databasen
        DirectorDAO directorDAO = new DirectorDAO(emf);
        MovieDAO movieDAO = new MovieDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);
        MovieGenreDAO movieGenreDAO = new MovieGenreDAO(emf);
        ActorDAO actorDAO = new ActorDAO(emf);

        // Opret en trådpool med 10 tråde for at kunne processere flere film parallelt
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // // Loop igennem alle film i listen
        for (MovieDTO movieDTO : movies) {
            // Submit en task til trådpoolen, som processerer én film
            executor.submit(() -> processMovie(movieDTO, directorService, genreService, actorService,
                    directorDAO, movieDAO, genreDAO, movieGenreDAO, actorDAO));
        }

        // Stop trådpoolen fra at acceptere nye tasks
        executor.shutdown();
        // Vent op til 10 minutter på at alle tråde er færdige
        executor.awaitTermination(10, TimeUnit.MINUTES);
    }

    // Hovedmetode for at processere én film
    private void processMovie(MovieDTO movieDTO, DirectorService directorService, GenreService genreService, ActorService actorService, DirectorDAO directorDAO, MovieDAO movieDAO, GenreDAO genreDAO, MovieGenreDAO movieGenreDAO, ActorDAO actorDAO) {

        // 1. Hent data fra API og sæt i MovieDTO
        List<DirectorDTO> directors = directorService.getDirectorsByMovieId(movieDTO.getId());
        if (directors.isEmpty()) return; // Hvis ingen director findes, stop processing af denne film

        // Tag den første director fra listen
        DirectorDTO directorDTO = directors.get(0);

        // Sæt director i MovieDTO
        movieDTO.setDirectorDTO(directorDTO);

        // Hent genrer for filmen (fra api)
        List<GenreDTO> genres = genreService.getGenreInfo(movieDTO.getId());
        movieDTO.setGenreDTO(genres); // Sæt genrer i MovieDTO

        // Hent skuespillere for filmen
        List<ActorDTO> actors = actorService.getActorInfo(movieDTO.getId());
        if (!actors.isEmpty()) {
            // Tag den første skuespiller og sæt i MovieDTO
            movieDTO.setActorDTO(actors.get(0));

            // Gem skuespilleren i DB, hvis den ikke allerede findes
            saveActorIfNotExists(actors.get(0), actorDAO);
        }

        // 2. Gem director i DB
        Director directorEntity = saveDirectorIfNotExists(directorDTO, directorDAO);

        // 3. Gem movie og relationer i DB
        saveMovieAndGenres(movieDTO, directorEntity, movieDAO, genreDAO, movieGenreDAO);
    }

    // Hjælpemetoder ⬇️

    // Gem en skuespiller i DB, hvis den ikke findes
    private void saveActorIfNotExists(ActorDTO actorDTO, ActorDAO actorDAO) {
        // Tjek om skuespilleren allerede findes i DB
        Actor actorEntity = actorDAO.getByActorId(actorDTO.getActor_id());
        if (actorEntity == null) { // Hvis ikke, opret ny entity og gem
            actorEntity = ActorMapper.toEntity(actorDTO);
            actorDAO.creat(actorEntity);
        }
    }

    // Tjek om director allerede findes i DB
    private Director saveDirectorIfNotExists(DirectorDTO directorDTO, DirectorDAO directorDAO) {
        try {
            return directorDAO.getById(directorDTO.getId());
        } catch (Exception e) {
            // Hvis director ikke findes, opret ny entity og gem
            Director directorEntity = DirectorMapper.toEntity(directorDTO);
            directorDAO.creat(directorEntity);
            return directorEntity;
        }
    }

    // Gem film og dens genrer i DB
    private void saveMovieAndGenres(MovieDTO movieDTO, Director directorEntity, MovieDAO movieDAO, GenreDAO genreDAO, MovieGenreDAO movieGenreDAO) {
        // Konverter MovieDTO til Movie entity
        Movie movieEntity = MovieMapper.toEntity(movieDTO);

        // Sæt director for filmen
        movieEntity.setDirector(directorEntity);

        // Gem filmen i DB
        movieDAO.creat(movieEntity);

        // Loop igennem alle genrer for filmen
        for (GenreDTO genreDTO : movieDTO.getGenreDTO()) {
            Genre genreEntity;
            try {
                // Tjek om genren allerede findes i DB
                genreEntity = genreDAO.getById(genreDTO.getId());
            } catch (Exception e) {
                genreEntity = GenreMapper.toEntity(genreDTO);
                genreDAO.creat(genreEntity);
            }

            // Opret relation mellem film og genre
            MovieGenre mg = new MovieGenre();
            mg.setMovie(movieEntity);
            mg.setGenre(genreEntity);
            movieGenreDAO.create(mg);
            movieEntity.getMovieGenres().add(mg);
        }
    }
}