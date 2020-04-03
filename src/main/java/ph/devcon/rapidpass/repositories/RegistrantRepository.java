package ph.devcon.rapidpass.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.devcon.rapidpass.entities.Registrant;

import java.util.List;

public interface RegistrantRepository extends JpaRepository<Registrant, Integer> {
    //TODO: Update the repository if needed

    List<Registrant> findAll();

    Registrant findById(String id);

    Registrant findByReferenceId(String referenceId);

    Registrant findByMobile(String mobileNumber);
}
