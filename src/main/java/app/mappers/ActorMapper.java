package app.mappers;

import app.dtos.ActorDTO;
import app.entities.Actor;
import app.entities.MovieCast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActorMapper {

    public static Actor toEntity(ActorDTO actorDTO){
        Actor actor = new Actor();
        actor.setActorId(actorDTO.getActor_id());
        actor.setDepartment(actorDTO.getKnown_for_department());
        actor.setName(actorDTO.getName());
        actor.setCastId(actorDTO.getCast_id());

        Set<MovieCast> actors = new HashSet<>();
        for(String a : actorDTO.getCharacter()){
            MovieCast movieCast = new MovieCast();
            movieCast.setActor(actor);
            movieCast.setCharacterName(a);
            actors.add(movieCast);
        }
        actor.setMovieCasts(actors);



        return actor;
    }
}
