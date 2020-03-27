package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.Databasechangeloglock;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Databasechangeloglock, Integer> {

    List<Databasechangeloglock> findByReferenceId(String referenceId);

}
