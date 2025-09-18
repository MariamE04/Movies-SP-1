package app.services;

import app.config.HibernateConfig;
import app.daos.DirectorDAO;
import app.dtos.DirectorDTO;
import app.entities.Director;
import app.mappers.DirectorMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class DirectorService {
    private ApiServices apiServices = new ApiServices();
    private JsonToDTOConverters jsonToDTOConverters = new JsonToDTOConverters();
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    DirectorDAO directorDAO = new DirectorDAO(emf);

    // Henter director(er) for en bestemt film baseret på movieId
    public List<DirectorDTO> getDirectorsByMovieId(int movieId) {
        String apiKey = System.getenv("API_KEY");
        String uri = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey;

        String json = apiServices.fetchFromApi(uri);

        // Mapper JSON -> liste af DirectorDTO
        return jsonToDTOConverters.toDirectorDTO(json);
    }


    /// Brug af DAO metoder:
    public Optional<DirectorDTO> getDirectorByName(String name){
        return directorDAO.findByName(name).map(DirectorMapper::toDTO);

    }

    public List<Director> getAllDirectors(){
        return directorDAO.getAll();
    }

    public void update(Director director){
        directorDAO.update(director);
    }

    public Director getById(int id){
        Director found = directorDAO.getByDirectorId(id);
        return  found;
    }

}
