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

    public List<ActorDTO> toActorDTO(String json){
        try {
            JsonNode root = objectMapper.readTree(json);
            List<ActorDTO> actorDTOS = new ArrayList<>();
            JsonNode castArray = root.get("cast");
            if (castArray != null){
                for (JsonNode castMember : castArray){
                    ActorDTO dto = new ActorDTO();

                    dto.setActor_id(castMember.get("id").asInt());
                    dto.setName(castMember.get("name").asText());
                    dto.setKnown_for_department(castMember.get("known_for_department").asText());
                    dto.setCast_id(castMember.get("cast_id").asInt());


                    JsonNode character = castMember.get("character");
                    if(character != null){
                        List<String> characters = new ArrayList<>();
                        characters.add(character.asText());
                        dto.setCharacter(characters);
                    }
                    actorDTOS.add(dto);
                }
            }
            return actorDTOS; // Den siger: “Læs denne JSON-streng og map den ind i et objekt af typen WeatherWrapper”.
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
                if("Director".equals(crewMember.get("job").asText()) &&
                        "Directing".equals(crewMember.get("known_for_department").asText())){

                    // 6) Hvis ja — laves der en ny DTO, udfylder felter fra JSON og tilføjer til listen
                    DirectorDTO dto = new DirectorDTO();
                    dto.setId((crewMember.get("id").asInt()));
                    dto.setName(crewMember.get("name").asText());
                    dto.setKnown_for_department(crewMember.get("known_for_department").asText());
                    dto.setJob(crewMember.get("job").asText());
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
    public int extractTotalPages(String json) { // Returnerer antal sider fra JSON
        try {
            ObjectMapper mapper = new ObjectMapper(); // Gør det muligt at læse JSON (ellers kunne java ikke forstå det)
            JsonNode root = mapper.readTree(json);    // Læser JSON-strengen til et JSON-træ (med JSON-træ kan man navigere direkte til det felt, man vil have uden at skulle “splitte strengen manuelt”.
            return root.get("total_pages").asInt();   // Finder "total_pages" og konverterer til tal
        } catch (Exception e) {
            e.printStackTrace(); // Printer fejl, hvis JSON ikke kan læses
            return 1;
        }
    }
}
