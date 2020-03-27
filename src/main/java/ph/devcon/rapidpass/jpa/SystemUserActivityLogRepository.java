package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.SystemUserActivityLog;

import java.util.List;

@Repository
public interface SystemUserActivityLogRepository extends JpaRepository<SystemUserActivityLog, Integer> {

    List<SystemUserActivityLog> findByReferenceId(String referenceId);

}
