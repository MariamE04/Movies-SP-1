package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

@Entity
public class MovieCast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String characterName;

    @ManyToOne
    private Actor actor;

    @ManyToOne
    private Movie movie;
}
