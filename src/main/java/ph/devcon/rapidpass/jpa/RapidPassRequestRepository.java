package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.RapidPassRequest;

import java.util.List;

@Repository
public interface RapidPassRequestRepository extends JpaRepository<RapidPassRequest, Integer> {

    List<RapidPassRequest> findByReferenceId(String referenceId);

}
