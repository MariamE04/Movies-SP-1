package app.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties (ignoreUnknown = true)
@Data
public class ActorDTO {

    @JsonProperty ("id")
    private Integer  actor_id;
    private String known_for_department;
    private String name;
    private List<String> character;
    private Integer  cast_id;


}
