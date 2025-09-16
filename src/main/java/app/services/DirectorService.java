package app.services;

import app.dtos.DirectorDTO;

import java.util.List;

public class DirectorService {
    private ApiServices apiServices = new ApiServices();
    private JsonToDTOConverters jsonToDTOConverters = new JsonToDTOConverters();

    // Henter director(er) for en bestemt film baseret på movieId
    public List<DirectorDTO> getDirectorsByMovieId(int movieId) {
        String apiKey = System.getenv("API_KEY");
        String uri = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey;

        String json = apiServices.fetchFromApi(uri);

        // Mapper JSON -> liste af DirectorDTO
        return jsonToDTOConverters.toDirectorDTO(json);
    }

}
