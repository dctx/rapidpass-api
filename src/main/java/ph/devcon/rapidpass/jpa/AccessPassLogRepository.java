package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.AccessPassLog;

import java.util.List;

@Repository
public interface AccessPassLogRepository extends JpaRepository<AccessPassLog, Integer> {
    //TODO: Update the repository if needed
    List<AccessPassLog> findAll();

    AccessPassLog findById(String id);
}
