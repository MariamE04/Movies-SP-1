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

import java.util.ArrayList;
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

    public List<DirectorDTO> toDirectorDTO(String json){
        try {
            // 1) Læs JSON-strengen ind i Jacksons "tree" model (JsonNode)
            JsonNode root = objectMapper.readTree(json);

            // 2) Hent "crew"-noden (det er et array i TMDb /credits-responsen)
            JsonNode crewArray  = root.get("crew");

            // 3) Forbered listen vi vil returnere
            List<DirectorDTO> directors = new ArrayList<>();

            // 4) Gå igennem hvert element i crew-arrayet
            for(JsonNode crewMember : crewArray){ // hvert crewMember er et JsonNode der repræsenterer et objekt i crew-arrayet

                // 5) Tjekker om denne crew-medlems 'job' er "Director"
                if("Director".equals(crewMember.get("job").asText())){

                    // 6) Hvis ja — laves der en ny DTO, udfylder felter fra JSON og tilføjer til listen
                    DirectorDTO dto = new DirectorDTO();
                    dto.setId((crewMember.get("id").asInt()));
                    dto.setName(crewMember.get("name").asText());
                    directors.add(dto);
                }
            }

            return directors;

        } catch (JsonProcessingException e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    public List<GenreDTO> toGenreDTO(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode resultsNode = root.get("genres"); // tag kun "results"
            return objectMapper.readValue(resultsNode.toString(), new TypeReference<List<GenreDTO>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
