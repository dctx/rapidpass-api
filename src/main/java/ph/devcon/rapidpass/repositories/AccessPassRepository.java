package ph.devcon.rapidpass.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.AccessPass;

import java.util.List;

@Repository
public interface AccessPassRepository extends JpaRepository<AccessPass, Integer> {
    List<AccessPass> findAll();

    AccessPass findByReferenceID(String referenceID);

    List<AccessPass> findAllByReferenceIDOrderByValidToDesc(String referenceId);
    
    AccessPass findByControlCode(String controlCode);
}
