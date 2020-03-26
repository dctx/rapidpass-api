package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.AccessPass;

@Repository
public interface AccessPassRepository extends JpaRepository<AccessPass, Integer> {
}
