package app.services;

import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Director;
import app.entities.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieService {


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

        for(MovieDTO movie: movies){
            List<DirectorDTO> directors = directorService.getDirectorsByMovieId(movie.getId());
            if(!directors.isEmpty()){
                movie.setDirectorDTO(directors.get(0));
            }
        }
    }

}
