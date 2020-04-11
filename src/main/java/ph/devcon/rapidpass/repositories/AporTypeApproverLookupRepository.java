package ph.devcon.rapidpass.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.devcon.rapidpass.entities.AporTypeApproverLookup;

import java.util.List;

public interface AporTypeApproverLookupRepository extends JpaRepository<AporTypeApproverLookup, Integer> {

    List<AporTypeApproverLookup> findAllByAporType(String aporType);

    AporTypeApproverLookup findByAporType(String aporType);

}
