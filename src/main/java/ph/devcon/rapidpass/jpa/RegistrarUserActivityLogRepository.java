package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.RegistrarUserActivityLog;

import java.util.List;

@Repository
public interface RegistrarUserActivityLogRepository extends JpaRepository<RegistrarUserActivityLog, Integer> {
    //TODO: Update repository if needed

    List<RegistrarUserActivityLog> findAll();

    RegistrarUserActivityLog findById(String id);
}
