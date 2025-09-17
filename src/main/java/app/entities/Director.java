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
@Table(
        name = "director",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "directorId")
        }
)

public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name ="directorId", nullable = false)
    private int directorId;
    private String department;
    private String name;
    private String job;

    @OneToMany(mappedBy = "director")
    @Builder.Default
    @ToString.Exclude
    private Set<Movie> movies  = new HashSet<>();


}
