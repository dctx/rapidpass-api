package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.RegistrarUserActivityLog;

import java.util.List;

@Repository
public interface RegistrarUserActivityLogRepository extends JpaRepository<RegistrarUserActivityLog, Integer> {

    List<RegistrarUserActivityLog> findByReferenceId(String referenceId);

}
