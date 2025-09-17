package app.services;

import app.config.HibernateConfig;
import app.daos.DirectorDAO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Director;
import app.entities.Movie;
import app.mappers.DirectorMapper;
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
        String json = apiServices.fetchFromApi(uri);
        return jsonToDTOConverters.toMovieDTOs(json);
    }

    public void MoviesWithDirectors(List<MovieDTO> movies){
        DirectorService directorService = new DirectorService();
        DirectorDAO directorDAO = new DirectorDAO(emf);

        for(MovieDTO movie: movies){
            // Henter director fra TMDb
            List<DirectorDTO> directors = directorService.getDirectorsByMovieId(movie.getId());
            if(!directors.isEmpty()){
                DirectorDTO directorDTO = directors.get(0);
                movie.setDirectorDTO(directorDTO);

                // Gem director i DB, hvis den ikke allerede findes
                try {
                    Director existing = directorDAO.getById(directorDTO.getId()); // TMDb ID
                } catch (Exception e) {
                    // Director findes ikke → gem
                    Director newDirector = DirectorMapper.toEntity(directorDTO);
                    directorDAO.creat(newDirector);
                }
            }
        }
    }


}
