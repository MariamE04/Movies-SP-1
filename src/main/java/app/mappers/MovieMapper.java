package app.mappers;

import app.dtos.ActorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.*;

import java.util.HashSet;
import java.util.Set;

public class MovieMapper {

    public static Movie toEntity(MovieDTO dto){
        Movie movie = new Movie();
        movie.setMovieId(dto.getId());
        movie.setOriginal_language(dto.getOriginal_language());
        movie.setOriginal_title(dto.getOriginal_title());
        movie.setOverview(dto.getOverview());
        movie.setPopularity(dto.getPopularity());
        movie.setRelease_date(dto.getRelease_date());
        movie.setTitle(dto.getTitle());
        movie.setVote_average(dto.getVote_average());
        movie.setVote_count(dto.getVote_count());

        movie.setDirector(DirectorMapper.toEntity(dto.getDirectorDTO()));
        return movie;

    }

}
