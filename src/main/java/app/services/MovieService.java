package app.services;

import app.config.HibernateConfig;
import app.daos.DirectorDAO;
import app.daos.MovieDAO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Director;
import app.entities.Movie;
import app.mappers.DirectorMapper;
import app.mappers.MovieMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

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

    public void MoviesWithDirectors(List<MovieDTO> movies){
        DirectorService directorService = new DirectorService();
        DirectorDAO directorDAO = new DirectorDAO(emf);
        MovieDAO movieDAO = new MovieDAO(emf);

        for(MovieDTO movieDTO: movies){
            // Henter director fra TMDb
            List<DirectorDTO> directors = directorService.getDirectorsByMovieId(movieDTO.getId());
            if(!directors.isEmpty()){
                DirectorDTO directorDTO = directors.get(0);
                movieDTO.setDirectorDTO(directorDTO);

                // Gem director i DB, hvis den ikke allerede findes
                Director directorEntity;

                try {
                    directorEntity = directorDAO.getById(directorDTO.getId());
                } catch (Exception e) {
                    // Director findes ikke → gem
                    directorEntity = DirectorMapper.toEntity(directorDTO);
                    directorDAO.creat(directorEntity);
                }

                // Lav Movie-entity fra DTO og sæt director
                Movie movieEntity = MovieMapper.toEntity(movieDTO);
                movieEntity.setDirector(directorEntity);

                // Gem filmen i DB
                movieDAO.creat(movieEntity);

            }
        }
    }


}
