package app.dtos;

import app.entities.Genre;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties (ignoreUnknown = true)
public class MovieDTO {

    private int id;
    private String original_language;
    private List<Integer> genre_ids;
    private String original_title;
    private String overview;
    private double popularity;
    private LocalDate release_date;
    private String title;
    private double vote_average;
    private double vote_count;
    private ActorDTO actorDTO;
    private DirectorDTO directorDTO;

}
