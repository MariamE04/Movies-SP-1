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
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int directorId;
    private String department;
    private String name;
    private String job;

    @OneToMany(mappedBy = "director")
    private Set<Movie> director = new HashSet<>();


}
