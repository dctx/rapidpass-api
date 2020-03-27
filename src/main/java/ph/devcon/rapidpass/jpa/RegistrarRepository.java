package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.Registrar;

import java.util.List;

@Repository
public interface RegistrarRepository extends JpaRepository<Registrar, Integer> {

    List<Registrar> findByReferenceId(String referenceId);

}
