package app.services;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.exceptions.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

public class JsonToDTOConverters {
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());;

        public List<MovieDTO> toMovieDTOs(String json){
            try {
                JsonNode root = objectMapper.readTree(json);
                JsonNode resultsNode = root.get("results"); // tag kun "results"
                return objectMapper.readValue(resultsNode.toString(), new TypeReference<List<MovieDTO>>() {});
            } catch (Exception e) {
                throw new RuntimeException(e);
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
