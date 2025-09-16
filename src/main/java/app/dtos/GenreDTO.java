package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties (ignoreUnknown = true)
@Data
public class GenreDTO {

    private String name;
    private int id;

}
