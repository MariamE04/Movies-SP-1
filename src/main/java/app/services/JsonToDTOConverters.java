package app.services;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.exceptions.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToDTOConverters {
    private ObjectMapper objectMapper = new ObjectMapper();

    public MovieDTO toMovieDTO(String json){
        try {
            return objectMapper.readValue(json, MovieDTO.class); // Den siger: “Læs denne JSON-streng og map den ind i et objekt af typen WeatherWrapper”.
        } catch (JsonProcessingException e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    public ActorDTO toActorDTO(String json){
        try {
            return objectMapper.readValue(json, ActorDTO.class); // Den siger: “Læs denne JSON-streng og map den ind i et objekt af typen WeatherWrapper”.
        } catch (JsonProcessingException e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    public DirectorDTO toDirectorDRO(String json){
        try {
            return objectMapper.readValue(json, DirectorDTO.class); // Den siger: “Læs denne JSON-streng og map den ind i et objekt af typen WeatherWrapper”.
        } catch (JsonProcessingException e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    public GenreDTO toGenreDRO(String json){
        try {
            return objectMapper.readValue(json, GenreDTO.class); // Den siger: “Læs denne JSON-streng og map den ind i et objekt af typen WeatherWrapper”.
        } catch (JsonProcessingException e) {
            throw new ApiException(500, e.getMessage());
        }
    }

}
