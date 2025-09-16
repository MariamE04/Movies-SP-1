package app.mappers;

import app.dtos.GenreDTO;
import app.entities.Genre;

public class GenreMapper {

    public static Genre toEntity(GenreDTO dto){
        Genre genre = new Genre();
        genre.setName(dto.getName());
        genre.setGenreId(dto.getId());

        return genre;

    }

}
