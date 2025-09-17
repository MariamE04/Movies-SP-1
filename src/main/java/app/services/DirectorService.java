package app.services;

import app.config.HibernateConfig;
import app.daos.DirectorDAO;
import app.dtos.DirectorDTO;
import app.mappers.DirectorMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class DirectorService {
    private ApiServices apiServices = new ApiServices();
    private JsonToDTOConverters jsonToDTOConverters = new JsonToDTOConverters();
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    // Henter director(er) for en bestemt film baseret på movieId
    public List<DirectorDTO> getDirectorsByMovieId(int movieId) {
        String apiKey = System.getenv("API_KEY");
        String uri = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey;

        String json = apiServices.fetchFromApi(uri);

        // Mapper JSON -> liste af DirectorDTO
        return jsonToDTOConverters.toDirectorDTO(json);
    }

    public Optional<DirectorDTO> getDirectorByName(String name){
        DirectorDAO directorDAO = new DirectorDAO(emf);
        return directorDAO.findByName(name).map(DirectorMapper::toDTO);

    }

}
