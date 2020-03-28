package ph.devcon.rapidpass.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.AccessPass;

import java.util.List;

@Repository
public interface AccessPassRepository extends JpaRepository<AccessPass, Integer> {
    List<AccessPass> findAll();

    AccessPass findByReferenceId(String referenceId);

    List<AccessPass> findAllByReferenceIdOrderByValidToDesc(String referenceId);
}
