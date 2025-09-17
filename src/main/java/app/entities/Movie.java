package app.entities;

import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.mappers.GenreMapper;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int movieId;
    private String original_language;
    private String original_title;
    private String overview;
    private double popularity;
    private LocalDate release_date;
    private String title;
    private double vote_average;
    private double vote_count;

    @OneToMany(mappedBy = "movie")
    @Builder.Default
    @ToString.Exclude
    private Set<MovieCast> moviesCasts = new HashSet<>();

    @OneToMany(mappedBy = "movie")
    @Builder.Default
    @ToString.Exclude
    Set<MovieGenre> movieGenres = new HashSet<>();

    @ManyToOne
    private Director director;
}
