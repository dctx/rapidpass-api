package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.AccessPassLog;

import java.util.List;

@Repository
public interface AccessPassLogRepository extends JpaRepository<AccessPassLog, Integer> {

    List<AccessPassLog> findByReferenceId(String referenceId);

}
