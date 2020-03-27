package ph.devcon.rapidpass.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.AccessPass;

import java.util.List;

@Repository
public interface AccessPassRepository extends PagingAndSortingRepository<AccessPass, Integer> {
    List<AccessPass> findAll();
}
