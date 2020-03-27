package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ph.devcon.rapidpass.model.Region;
import ph.devcon.rapidpass.model.Registrant;

import java.util.List;

public interface RegistrantRepository extends JpaRepository<Registrant, Integer> {
    //TODO: Update the repository if needed

    List<Registrant> findAll();

    Registrant findById(String id);
}
