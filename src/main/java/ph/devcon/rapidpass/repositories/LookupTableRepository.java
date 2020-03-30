package ph.devcon.rapidpass.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.LookupTable;

import java.util.List;

@Repository
public interface LookupTableRepository extends PagingAndSortingRepository<LookupTable, String> {

    public List<LookupTable> getAllByLookupTablePKKey(String key);
}
