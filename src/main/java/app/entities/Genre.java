package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

@Entity
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int genreId;
    private String name;

    @OneToMany(mappedBy = "genre")
    @Builder.Default
    @ToString.Exclude
    private Set<MovieGenre> movieGenres  = new HashSet<>();

}
