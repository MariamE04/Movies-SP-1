package app.services;

import app.dtos.GenreDTO;

import java.util.List;

public class GenreService {
    private ApiServices apiServices = new ApiServices();
    private JsonToDTOConverters jsonToDTOConverters = new JsonToDTOConverters();

    public List<GenreDTO> getGenreInfo(int movieId){
        String apiKey = System.getenv("API_KEY");
        String uri = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey + "&language=da";
        String json = apiServices.fetchFromApi(uri);

        return jsonToDTOConverters.toGenreDTO(json);
    }

}
