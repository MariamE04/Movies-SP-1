package app.services;

import app.config.HibernateConfig;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.entities.MovieGenre;
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
       // Her oprettes service/DAO objekter, som vi skal bruge til at:
        DirectorService directorService = new DirectorService(); // Hente director-data fra API
        GenreService genreService = new GenreService();
        DirectorDAO directorDAO = new DirectorDAO(emf); //  Håndtere directors i databasen (gem/pesister)
        MovieDAO movieDAO = new MovieDAO(emf); //Håndtere movies i databasen (gem/pesister)
        GenreDAO genreDAO = new GenreDAO(emf);

        // Opret tråd-pool med 10 tråde → parallelt arbejde
        ExecutorService executor = Executors.newFixedThreadPool(10);

        //For hver film i listen: sender vi en opgave til executor (en ledig tråd) -> hente director + gemme filmen i DB.
        for(MovieDTO movieDTO: movies){
            executor.submit(()->{
            // Hent directors fra API (gemmes i list)
            List<DirectorDTO> directors = directorService.getDirectorsByMovieId(movieDTO.getId());
            if(!directors.isEmpty()){ // Hvis ikke tom → tag første director
                DirectorDTO directorDTO = directors.get(0);
                movieDTO.setDirectorDTO(directorDTO); // Gem directorDTO i movieDTO (sener brug)

                List<GenreDTO> genres = genreService.getGenreInfo(movieDTO.getId());
                movieDTO.setGenreDTO(genres);

                // Gem director i DB, hvis den ikke allerede findes
                Director directorEntity;

                try {
                    directorEntity = directorDAO.getById(directorDTO.getId());
                } catch (Exception e) {
                    // // Tjek om director findes i DB, ellers gem
                    directorEntity = DirectorMapper.toEntity(directorDTO);
                    directorDAO.creat(directorEntity);
                }

                // Konverterer MovieDTO til Movie entity, som kan gemmes i DB.
                Movie movieEntity = MovieMapper.toEntity(movieDTO);
                movieEntity.setDirector(directorEntity);

                // Persisterer filmen med director til databasen. (gem)
                movieDAO.creat(movieEntity);

                // --- Gem genre ---
                for (GenreDTO genreDTO : genres) {
                    try {
                        Genre genreEntity = genreDAO.getById(genreDTO.getId()); // prøv at finde
                        // Tilføj forbindelse
                        MovieGenre mg = new MovieGenre();
                        mg.setMovie(movieEntity);
                        mg.setGenre(genreEntity);
                        movieEntity.getMovieGenres().add(mg);
                    } catch (Exception e) {
                        // hvis ikke findes, gem ny
                        Genre genreEntity = GenreMapper.toEntity(genreDTO);
                        genreDAO.creat(genreEntity);

                        MovieGenre mg = new MovieGenre();
                        mg.setMovie(movieEntity);
                        mg.setGenre(genreEntity);
                        movieEntity.getMovieGenres().add(mg);
                    }
                }

            }
            }); // Slut på executor-opgaven for én film.
        }

        // sikrer, at alle film er hentet og gemt, før metoden afslutter.
        executor.shutdown(); // vi sender ikke flere opgaver til executor.
        executor.awaitTermination(10, TimeUnit.MINUTES); // venter op til 10 minutter, indtil alle tråde er færdige.

    }
}
