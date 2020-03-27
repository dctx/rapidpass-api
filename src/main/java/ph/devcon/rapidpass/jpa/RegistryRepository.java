package ph.devcon.rapidpass.jpa;

import org.springframework.data.repository.CrudRepository;
import ph.devcon.rapidpass.model.Registrar;

public interface RegistryRepository extends CrudRepository<Registrar, Integer> {

}
