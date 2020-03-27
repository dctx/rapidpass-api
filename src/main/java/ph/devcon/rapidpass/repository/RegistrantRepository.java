package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.devcon.rapidpass.model.Registrant;

public interface RegistrantRepository extends JpaRepository<Registrant, Integer> {

}
