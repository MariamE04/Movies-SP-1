package app.services;

import app.dtos.ActorDTO;

import java.util.List;

public class ActorService {
    ApiServices apiServices = new ApiServices();
    JsonToDTOConverters jsonToDTOConverters = new JsonToDTOConverters();


    public List<ActorDTO> getActorInfo(int movieId){
        String apiKey = System.getenv("API_KEY");
        String uri = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey;

        String json = apiServices.fetchFromApi(uri);

        return jsonToDTOConverters.toActorDTO(json);
    }
}
