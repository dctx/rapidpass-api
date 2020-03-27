package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.AccessPass;

import java.util.List;

@Repository
public interface AccessPassRepository extends JpaRepository<AccessPass, Integer> {
    List<AccessPass> findAll();

    AccessPass findByReferenceId(String referenceId);

    List<AccessPass> findAllByReferenceIdOrderByValidToDesc(String referenceId);
}
