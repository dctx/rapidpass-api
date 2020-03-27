package ph.devcon.rapidpass.repository;

import org.springframework.data.repository.CrudRepository;
import ph.devcon.rapidpass.model.Registrar;

public interface RegistryRepository extends CrudRepository<Registrar, Integer> {

}
