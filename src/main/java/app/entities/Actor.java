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
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int actorId;
    private String department;
    private String name;
    private int castId;

    @OneToMany(mappedBy = "actor")
    @Builder.Default
    @ToString.Exclude
    private Set<MovieCast> actors = new HashSet<>();
}
