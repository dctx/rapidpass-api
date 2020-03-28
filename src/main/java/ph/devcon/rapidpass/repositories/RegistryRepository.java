package ph.devcon.rapidpass.repositories;

import org.springframework.data.repository.CrudRepository;
import ph.devcon.rapidpass.entities.Registrar;

public interface RegistryRepository extends CrudRepository<Registrar, Integer> {

}
