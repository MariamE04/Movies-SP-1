package app.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties (ignoreUnknown = true)
@Data
public class ActorDTO {

    @JsonProperty ("id")
    private int actor_id;
    private String known_for_department;
    private String name;
    private String character;
    private int cast_id;


}
