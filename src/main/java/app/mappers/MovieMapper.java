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


        //TODO: Har ikke testet det men MovieCast  forbinder actor og movie sammen her
        Set<MovieCast> movieCasts = new HashSet<>();


        if(dto.getDirectorDTO() != null){
            Director director = DirectorMapper.toEntity(dto.getDirectorDTO());
            movie.setDirector(director);
        }


        if (dto.getActorDTO() != null) {
            Actor actor = ActorMapper.toEntity(dto.getActorDTO());
            MovieCast mc = new MovieCast();
            mc.setActor(actor);
            mc.setMovie(movie);
            movieCasts.add(mc);
            movie.setMoviesCasts(movieCasts);
        }

        // Opretter et tomt Set til at holde relationerne mellem film og genre
       /* Set<MovieGenre> movieGenres = new HashSet<>();

        // Løkke over alle genreDTO’er, som kommer fra MovieDTO
        for (GenreDTO genreDTO : dto.getGenreDTO()) {

            // Konverterer GenreDTO til en Genre-entity (fra DTO-lag til DB-lag)
            Genre genre = GenreMapper.toEntity(genreDTO);

            // Opretter et nyt MovieGenre-objekt (join-entity mellem Movie og Genre)
            MovieGenre mg = new MovieGenre();

            // Sætter filmen på relationen (kobler Movie til MovieGenre)
            mg.setMovie(movie);

            // Sætter genren på relationen (kobler Genre til MovieGenre)
            mg.setGenre(genre);

            // Tilføjer den færdige relation til sættet
            movieGenres.add(mg);
        }
        // Når alle genre er mappet, sætter vi hele mængden af MovieGenre ind på Movie
        movie.setMovieGenres(movieGenres); */


        //midlertidig løsning gem film i db uden genre
        Set<MovieGenre> movieGenres = new HashSet<>();

        if (dto.getGenreDTO() != null) {   // <-- tjek om genreDTO er null
            for (GenreDTO genreDTO : dto.getGenreDTO()) {
                Genre genre = GenreMapper.toEntity(genreDTO);
                MovieGenre mg = new MovieGenre();
                mg.setMovie(movie);
                mg.setGenre(genre);
                movieGenres.add(mg);
            }
        }

        return movie;

    }

}
