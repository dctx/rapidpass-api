package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.SystemUserActivityLog;

import java.util.List;

@Repository
public interface SystemUserActivityLogRepository extends JpaRepository<SystemUserActivityLog, Integer> {
    //TODO: Update the repository if needed

    List<SystemUserActivityLog> findAll();

    SystemUserActivityLog findById(String referenceId);
}
