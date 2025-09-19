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

    public void processor(List<MovieDTO> movies) throws InterruptedException {
        // 1) Opret services og DAO'er som genbruges af alle tråde
        // Disse objekter bruges til at hente data fra API'er og til at gemme i databasen.
        DirectorService directorService = new DirectorService();
        GenreService genreService = new GenreService();
        DirectorDAO directorDAO = new DirectorDAO(emf);
        MovieDAO movieDAO = new MovieDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);
        MovieGenreDAO movieGenreDAO = new MovieGenreDAO(emf);

        // 2) Opret en tråd-pool (executor) så vi kan køre opgaver parallelt
        // FixedThreadPool(10) betyder max 10 samtidige tråde/opgaver.
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // 3) For hver film: opret en opgave og giv den til executor
        for (MovieDTO movieDTO : movies) {
            executor.submit(() -> handleSingleMovie(movieDTO, directorService, genreService, directorDAO, movieDAO, genreDAO, movieGenreDAO));
        }

        // 4) Stop med at acceptere nye opgaver og vent til alle er færdige
        executor.shutdown(); // sender signal om "ingen flere opgaver"
        executor.awaitTermination(10, TimeUnit.MINUTES); // venter op til 10 min
    }

    private void handleSingleMovie(MovieDTO movieDTO, DirectorService directorService, GenreService genreService, DirectorDAO directorDAO, MovieDAO movieDAO, GenreDAO genreDAO, MovieGenreDAO movieGenreDAO) {
        // A) Hent director(er) fra ekstern API for denne film
        List<DirectorDTO> directors = directorService.getDirectorsByMovieId(movieDTO.getId());
        // Hvis ingen directors fundet: intet mere at gøre for denne film
        if (!directors.isEmpty()) {
            // Tag den første director
            DirectorDTO directorDTO = directors.get(0);
            movieDTO.setDirectorDTO(directorDTO);

            // B) Hent genres fra ekstern API for denne film
            List<GenreDTO> genres = genreService.getGenreInfo(movieDTO.getId());
            movieDTO.setGenreDTO(genres);

            // C) Sørg for at director findes i DB: hent ellers opret
            Director directorEntity;
            try {
                // Forsøg at hente director fra DB (antager getById kaster hvis ikke fundet)
                directorEntity = directorDAO.getById(directorDTO.getId());
            } catch (Exception e) {
                // Hvis ikke fundet (eller andet problem der kastes), konverter og opret
                directorEntity = DirectorMapper.toEntity(directorDTO);
                directorDAO.creat(directorEntity);
            }

            // D) Konverter MovieDTO til Movie entity og sæt director-relation
            Movie movieEntity = MovieMapper.toEntity(movieDTO);

            // E) Gem filmen i DB
            movieEntity.setDirector(directorEntity);

            // Gem MovieGenre relationer for hver genre
            movieDAO.creat(movieEntity);

            saveMovieGenres(movieEntity, genres, genreDAO, movieGenreDAO);
        }
    }

    private void saveMovieGenres(Movie movieEntity, List<GenreDTO> genres, GenreDAO genreDAO, MovieGenreDAO movieGenreDAO) {
        // Hent/eller opret genre
        for (GenreDTO genreDTO : genres) {
            Genre genreEntity;
            try {
                genreEntity = genreDAO.getById(genreDTO.getId()); //henter via id
            } catch (Exception e) {
                genreEntity = GenreMapper.toEntity(genreDTO); // konvetter DTO -> entity
                genreDAO.creat(genreEntity); // gem/ persister
            }

            // Opret og gem MovieGenre
            MovieGenre mg = new MovieGenre();
            mg.setMovie(movieEntity);
            mg.setGenre(genreEntity);
            movieGenreDAO.create(mg);

            // Tilføj relation i memory
            movieEntity.getMovieGenres().add(mg);
        }
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

