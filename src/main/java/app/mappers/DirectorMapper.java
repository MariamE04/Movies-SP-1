package app.mappers;

import app.dtos.DirectorDTO;
import app.entities.Director;

public class DirectorMapper {

    public static  Director toEntity(DirectorDTO dto){
        if(dto == null){
return null;
        }
        Director director = new Director();
        director.setDirectorId(dto.getId());
        director.setDepartment(dto.getKnown_for_department());
        director.setName(dto.getName());
        director.setJob(dto.getJob());
        return director;
    }

    public static DirectorDTO toDTO(Director director) {
        DirectorDTO dto = new DirectorDTO();
        dto.setId(director.getDirectorId());
        dto.setName(director.getName());
        dto.setJob(director.getJob());
        dto.setKnown_for_department(director.getDepartment());
        return dto;
    }

}
