package app.mappers;

import app.dtos.DirectorDTO;
import app.entities.Director;

public class DirectorMapper {

    public static  Director toEntity(DirectorDTO dto){
        Director director = new Director();
        director.setDirectorId(dto.getId());
        director.setDepartment(dto.getKnown_for_department());
        director.setName(dto.getName());
        director.setJob(dto.getJob());
        return director;
    }

}
