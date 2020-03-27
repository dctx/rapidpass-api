package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ph.devcon.rapidpass.model.Registrant;

public interface RegistrantRepository extends JpaRepository<Registrant, Integer> {

}
