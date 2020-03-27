package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.City;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    //TODO: Update the repository if needed by the associated service
    List<City> findAll();

    City findById(String id);
}
