package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@JsonIgnoreProperties (ignoreUnknown = true)
@Data
public class DirectorDTO {

    private int id;
    private String known_for_department;
    private String name;
    private String job;

}
